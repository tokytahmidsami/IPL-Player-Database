package Project.exception_classes;
public class NotFoundException extends Exception{
    public NotFoundException(){
        super("Something not found");
    }

    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
