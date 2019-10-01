package no.ntnu.trostespel;

public class Router {

    private static Router instance = null;

    private Router() {
    }

    // static method to create instance of Singleton class
    public static Router getInstance() {
        if (instance == null)
            instance = new Router();
        return instance;
    }


}

