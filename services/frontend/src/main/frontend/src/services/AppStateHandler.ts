import React from 'react';
import {Interface} from "readline";


enum LoginLevel {
    loggedIn,
    loggedOut
}

class AuthHandler {
    private loginLevel: LoginLevel;

    constructor(loginLevel: LoginLevel) {
        this.loginLevel = loginLevel;
    }

    public isLoggedIn() {
        return this.loginLevel == LoginLevel.loggedIn;
    }
    
}

interface IAppState {

}


function getAppState() {

}


