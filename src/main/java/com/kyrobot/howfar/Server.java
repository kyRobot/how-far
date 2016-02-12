package com.kyrobot.howfar;

import spark.Request;
import spark.Response;
import spark.Route;
 
import static spark.Spark.*;

/**
 * Main App Entry point
 *
 */
public class Server 
{
    public static void main( String[] args )
    {
        get("/", (req, res) -> "Hello, World!");
    }
}