package com.maurice.DocumentManagement.exceptions;

public class CreateStatusException {

    public static RuntimeException getThrowableException(String errorMessage, int codeStatus){
        return switch (codeStatus) {
            case 400 -> new BadRequestException(errorMessage);
            case 404 -> new NotFoundException(errorMessage);
            case 406 -> new NotAcceptableRequestException(errorMessage);
            case 500 -> new InternalServerErrorException(errorMessage);
            default -> null;
        };
    }

}
