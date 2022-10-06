package jp.co.gutingjun.rpa.exception;

public class InvalidJobNodeException extends Exception{
    @Override
    public String getMessage() {
        return "Invalid job node foud:  job node must connect with linker.";
    }
}
