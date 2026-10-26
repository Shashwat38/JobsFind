package com.jobsfindai.backend.profile;

public class UserProfile {
    private String name;
    private String skill;

    public UserProfile(){
        this.name = "";
        this.skill = "";
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSkill(String skill){
        this.skill = skill;
    }

    public String getName(){
        return this.name;
    }

    public String getSkill(){
        return this.skill;
    }






}
