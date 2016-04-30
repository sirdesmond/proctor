package com.team2.android.proctor.model.input;

/**
 * Created by abhijeet on 4/8/16.
 */
public class GetThroughId {
    private int  id ;


    public GetThroughId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GetThroughId{" +
                "id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
