package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.*;

import java.sql.*;
import java.util.*;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@SpringBootApplication
public class Application {

    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

    @RequestMapping("/createuserform")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "createuser";
    }

    @RequestMapping("/users")
    public String users(Model model) {
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "SELECT id, first, last, email, company, city FROM cuser";
            ResultSet rs = stmt.executeQuery(sql);
            StringBuffer sb = new StringBuffer();
            List users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String first = rs.getString("first");
                String last = rs.getString("last");
                String email = rs.getString("email");
                String company = rs.getString("company");
                String city = rs.getString("city");
                users.add(new User(id,first, last, email, company, city));
            }
            model.addAttribute("users", users);
            return "user";
        } catch (Exception e) {
            return e.toString();
        }
    }

    @RequestMapping(value="/createuser", method=RequestMethod.POST)
    public String createUser(@ModelAttribute User user, Model model) {
        model.addAttribute("user", user);
        int id = user.getId();
        String first = user.getFirst();
        String last = user.getLast();
        String email = user.getEmail();
        String city = user.getCity();
        String company = user.getCompany();
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql;
            sql = "insert into cuser(first, last, email, company, city) values " +
                    "('" + first  + "', '" + last + " ',' " + email +  "', ' " +
                    company + "', '" + city + "');";
            ResultSet rs = stmt.executeQuery(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "result";
    }


    private static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = null;
        if(System.getenv("DATABASE_URL") != null) {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        }else {
            String DATABASE_URL = "postgres://ubuntu:ubuntu@localhost:5432/userdb";
            dbUri = new URI(DATABASE_URL);
        }

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath()
                + "?sslmode=require";
        /*Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/userdb?sslmode=require",
                "ubuntu",
                "ubuntu");*/
		return DriverManager.getConnection(dbUrl, username, password);
	}

	public static void main(String[] args) {
		SpringApplication.run(
                Application.class, args);
	}
}
