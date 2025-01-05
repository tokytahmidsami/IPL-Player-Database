package Project.auxiliary_classes;

import java.io.Serializable;

public class Player implements Serializable{
    private String name, country, club, position;
    private int age, joursey, weeklySalary, price;
    private double height;
    private boolean onSale;

    public Player(){};

    public Player(String name, String country, int age, double height, String club, 
                  String position, int joursey, int weeklySalary){
        this.name = name;
        this.country = country;
        this.age = age;
        this.height = height;
        this.club = club;
        this.position = position;
        this.joursey = joursey;
        this.weeklySalary = weeklySalary;
        this.onSale = false;
        this.price = 0;
    }

    public Player(Player p){
        this.name = p.name;
        this.country = p.country;
        this.age = p.age;
        this.height = p.height;
        this.club = p.club;
        this.position = p.position;
        this.joursey = p.joursey;
        this.weeklySalary = p.weeklySalary;
        this.onSale = false;
    }

    //getters
    public String getName(){ return name; }
    public String getCountry(){ return country; }
    public int getAge(){ return age; }
    public double getHeight(){ return height; }
    public String getClub(){ return club; }
    public String getPosition(){ return position; }
    public int getNumber(){ return joursey; }
    public int getWeeklySalary(){ return weeklySalary; }
    public boolean getOnSaleStatus(){ return onSale; }
    public int getPrice(){ return price; }

    //setters
    public void setName(String name){ this.name = name; }
    public void setCountry(String country){ this.country = country; }
    public void setAge(int age){ this.age = age; }
    public void setHeight(double height){ this.height = height; }
    public void setClub(String club){ this.club = club; }
    public void setPosition(String position){ this.position = position; }
    public void setNumber(int number){ this.joursey = number; }
    public void setWeeklySalary(int weeklySalary){ 
        this.weeklySalary = weeklySalary; 
    }
    public void setOnSaleStatus(boolean status){ this.onSale = status; }
    public void setPrice(int price){ this.price = price; }

    public void showDetails(){
        System.out.println();
        System.out.println("Name: " + name);
        System.out.println("Country: " + country);
        System.out.println("Age: " + age + " years");
        System.out.println("Height: " + height + " meter");
        System.out.println("Club: " + club);
        System.out.println("Position: " + position);
        System.out.println("Joursey Number: " + ((joursey > 0)? joursey: "N/A"));
        System.out.println("Weekly Salary: " + weeklySalary + " rupee");
        System.out.println("On Sale: " + onSale);
        System.out.println("Current Value: " + price);
    }
}