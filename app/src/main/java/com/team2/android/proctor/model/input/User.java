package com.team2.android.proctor.model.input;

import java.io.Serializable;

/**
 * Created by kofikyei on 4/29/16.
 */
public class User implements Serializable {
    private int code;
    private Long userId;

    public User(int code, Long userId) {
        this.code = code;
        this.userId = userId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
