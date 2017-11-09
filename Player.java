/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.io.Serializable;

/**
 *
 * @author NamNguyen
 */
public class Player implements Serializable{
    private int id;
    private String name;
    private int win;
    private int game;
    
    public Player(){
        
    }
    
    public Player(int id, String name,int win,int game){
        this.id=id;
        this.game=game;
        this.name=name;
        this.win=win;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id=id;
    }
    
    public int getWin(){
        return win;
    }
    
    public void setWin(int win){
        this.win=win;
    }
    public int getGame(){
        return game;
    }
    
    public void setGame(int game){
        this.game=game;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name=name;
    }
    
}
