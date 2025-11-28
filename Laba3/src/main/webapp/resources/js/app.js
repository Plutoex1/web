document.addEventListener('DOMContentLoaded', () => {
    const graph = document.getElementById('graph');
    if (!graph) return;

    graph.addEventListener('click', (e) => {
        const rRadio = document.querySelector('input[name="main-form:r-radio"]:checked');
        if (!rRadio) {
            if (window.PF && PF('growl')) {
                PF('growl').renderMessage({
                    summary: 'Ошибка',
                    detail: 'Для клика по графику сначала выберите R.',
                    severity: 'error'
                });
            }
            return;
        }
        const r = parseFloat(rRadio.value);

        const rect = graph.getBoundingClientRect();
        const svgX = e.clientX - rect.left;
        const svgY = e.clientY - rect.top;

        // Масштабный коэффициент, R = 40px
        const scaleFactor = 40;

        // Пересчитываем координаты из пикселей SVG в математические
        const x = (svgX - 160) / scaleFactor;
        const y = (160 - svgY) / scaleFactor;

        // Вызываем p:remoteCommand из JSF, передавая вычисленные параметры
        sendClick([{name: 'x', value: x}, {name: 'y', value: y}, {name: 'r', value: r}]);
    });
});