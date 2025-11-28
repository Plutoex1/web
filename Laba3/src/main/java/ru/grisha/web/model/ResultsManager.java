package ru.grisha.web.model;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("resultsManager")
@SessionScoped
public class ResultsManager implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    private HitResult newResult;
    private List<HitResult> results;
    private Double selectedX; // !!! ДОБАВЛЕНО !!!

    @PostConstruct
    public void init() {
        newResult = new HitResult();
        newResult.setR(1.0);
        selectedX = null;
        loadResults();
    }

    private void loadResults() {
        try {
            TypedQuery<HitResult> query = entityManager.createQuery(
                    "SELECT h FROM HitResult h ORDER BY h.id DESC", HitResult.class);
            results = new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки результатов из БД: " + e.getMessage());
            results = new ArrayList<>();
        }
    }

    @Transactional
    public void addResultFromForm() {
        processAndSave(newResult);
    }

    @Transactional
    public void addResultFromGraph() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        try {
            HitResult graphHit = new HitResult();
            graphHit.setX(Double.parseDouble(params.get("x")));
            graphHit.setY(Double.parseDouble(params.get("y")));
            graphHit.setR(Double.parseDouble(params.get("r")));
            processAndSave(graphHit);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга параметров с графика: " + e.getMessage());
        }
    }

    private void processAndSave(HitResult hit) {
        long startTime = System.nanoTime();
        hit.checkHit();
        hit.setServerTime(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        hit.setExecTimeNs(System.nanoTime() - startTime);

        entityManager.persist(hit);
        results.add(0, hit);

        newResult = new HitResult();
        newResult.setR(hit.getR());
        selectedX = null; // !!! ДОБАВЛЕНО: сброс после проверки !!!
    }

    @Transactional
    public void clearResults() {
        entityManager.createQuery("DELETE FROM HitResult").executeUpdate();
        results.clear();
    }

    // !!! ИСПРАВЛЕНО: возвращает String для navigation !!!
    public String setX(Double x) {
        this.selectedX = x;
        if (this.newResult != null) {
            this.newResult.setX(x);
        }
        return null;
    }

    // !!! ДОБАВЛЕНО: getter для selectedX !!!
    public Double getSelectedX() {
        return selectedX;
    }

    public void setSelectedX(Double selectedX) {
        this.selectedX = selectedX;
    }

    public HitResult getNewResult() { return newResult; }
    public void setNewResult(HitResult newResult) { this.newResult = newResult; }
    public List<HitResult> getResults() { return results; }
}