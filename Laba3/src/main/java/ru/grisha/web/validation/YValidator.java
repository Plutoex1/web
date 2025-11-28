package ru.grisha.web.validation;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator("yValidator")
public class YValidator implements Validator<Double> {
    @Override
    public void validate(FacesContext context, UIComponent component, Double value)
            throws ValidatorException {
        if (value == null) {
            return;
        }

        // Проверка диапазона
        if (value <= -3 || value >= 5) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка валидации",
                            "Y должен быть в диапазоне (-3; 5)."));
        }

        // !!! ДОБАВЛЕНО: проверка на разумную точность (макс 8 знаков после запятой) !!!
        String valueStr = value.toString();
        if (valueStr.contains(".")) {
            int decimalPlaces = valueStr.length() - valueStr.indexOf('.') - 1;
            if (decimalPlaces > 8) {
                throw new ValidatorException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Ошибка валидации",
                                "Y не может содержать больше 8 знаков после запятой."));
            }
        }
    }
}