package com.kloudspot.employee_management_system.exception;

public class DuplicateEmailException extends RuntimeException
{
    public DuplicateEmailException(String msg) { super(msg); }
}
