import React, {Component, Dispatch, SetStateAction, useEffect} from 'react';


export enum APP_STATES {
    LOGIN,
    MAIN_PAGE,
}

class ObservableNavigator {
    public currentState: APP_STATES;
    private listeners: Map<string, Function>;

    constructor() {
        this.currentState = APP_STATES.LOGIN;
        this.listeners = new Map<string, Function>();
    }


    public setAppState(newState: APP_STATES) {
        this.currentState = newState;
        this.listeners.forEach((value, key) => {
            value(newState);
        })
    }

    public registerListener(listenFunc: Function, id: string) {
        this.listeners.set(id, listenFunc)
    }
}

export const AppNavigator = new ObservableNavigator();



