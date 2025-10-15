package com.delivery_trujillo.app_trujillo_services.services.models.validations;

import com.delivery_trujillo.app_trujillo_services.persistence.entities.UserEntity;
import com.delivery_trujillo.app_trujillo_services.services.models.dtos.ResponseDTO;

public class UserValidations {

    // NUEVO REGEX QUE INCLUYE CARACTERES ESPECIALES COMO REQUISITO
    // Requiere: 8-16 caracteres, 1 mayúscula, 1 minúscula, 1 número, 1 carácter especial
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$";

    // Regex anterior (sin caracteres especiales) - por si quieres mantenerlo para compatibilidad
    private static final String LEGACY_PASSWORD_REGEX =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,16}$";

    public ResponseDTO validate(UserEntity users){
        ResponseDTO response = new ResponseDTO();

        response.setNumOfErrors(0);

        // Validación de email
        if (users.getEmail() == null ||
                !users.getEmail().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            response.setNumOfErrors(response.getNumOfErrors() + 1);
            response.setMessage("El campo email no es válido");
        }

        // Validación de nombre
        if (users.getFirstName() == null ||
                users.getFirstName().length() < 3 ||
                users.getFirstName().length() > 15){
            response.setNumOfErrors(response.getNumOfErrors() + 1);
            response.setMessage("El campo nombre debe tener entre 3 y 15 caracteres");
        }

        // Validación de apellido
        if (users.getLastName() == null ||
                users.getLastName().length() < 3 ||
                users.getLastName().length() > 30){
            response.setNumOfErrors(response.getNumOfErrors() + 1);
            response.setMessage("El campo apellido debe tener entre 3 y 30 caracteres");
        }

        // VALIDACIÓN DE CONTRASEÑA ACTUALIZADA
        if (users.getPassword() != null && !users.getPassword().matches(PASSWORD_REGEX)){
            response.setNumOfErrors(response.getNumOfErrors() + 1);
            response.setMessage("La contraseña debe tener entre 8 y 16 caracteres, " +
                    "al menos una mayúscula, una minúscula, un número " +
                    "y un carácter especial (!@#$%^&*...)");
        }

        return response;
    }

    // Método adicional para verificar si una contraseña cumple los requisitos
    public boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }

    // Método para obtener detalles de qué le falta a una contraseña
    public PasswordRequirements checkPasswordRequirements(String password) {
        PasswordRequirements req = new PasswordRequirements();

        if (password == null || password.isEmpty()) {
            req.setValid(false);
            return req;
        }

        req.setHasMinLength(password.length() >= 8);
        req.setHasMaxLength(password.length() <= 16);
        req.setHasUpperCase(password.matches(".*[A-Z].*"));
        req.setHasLowerCase(password.matches(".*[a-z].*"));
        req.setHasNumber(password.matches(".*\\d.*"));
        req.setHasSpecialChar(password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"));

        req.setValid(req.isHasMinLength() && req.isHasMaxLength() &&
                req.isHasUpperCase() && req.isHasLowerCase() &&
                req.isHasNumber() && req.isHasSpecialChar());

        return req;
    }

    // Clase interna para los requisitos
    public static class PasswordRequirements {
        private boolean hasMinLength;
        private boolean hasMaxLength;
        private boolean hasUpperCase;
        private boolean hasLowerCase;
        private boolean hasNumber;
        private boolean hasSpecialChar;
        private boolean isValid;

        // Getters y setters
        public boolean isHasMinLength() { return hasMinLength; }
        public void setHasMinLength(boolean hasMinLength) { this.hasMinLength = hasMinLength; }

        public boolean isHasMaxLength() { return hasMaxLength; }
        public void setHasMaxLength(boolean hasMaxLength) { this.hasMaxLength = hasMaxLength; }

        public boolean isHasUpperCase() { return hasUpperCase; }
        public void setHasUpperCase(boolean hasUpperCase) { this.hasUpperCase = hasUpperCase; }

        public boolean isHasLowerCase() { return hasLowerCase; }
        public void setHasLowerCase(boolean hasLowerCase) { this.hasLowerCase = hasLowerCase; }

        public boolean isHasNumber() { return hasNumber; }
        public void setHasNumber(boolean hasNumber) { this.hasNumber = hasNumber; }

        public boolean isHasSpecialChar() { return hasSpecialChar; }
        public void setHasSpecialChar(boolean hasSpecialChar) { this.hasSpecialChar = hasSpecialChar; }

        public boolean isValid() { return isValid; }
        public void setValid(boolean isValid) { this.isValid = isValid; }
    }

}
