package Project.exception_classes;

public class DuplicateException extends Exception{
    public DuplicateException(){
        super("Something being illegally duplicated");
    }

    public DuplicateException(String message){
        super(message);
    }

    public DuplicateException(String message, Throwable cause){
        super(message, cause);
    }
}
