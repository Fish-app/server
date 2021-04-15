import React, {Component, Dispatch, SetStateAction, useEffect} from 'react';
import logo from './logo.svg';
import './App.css';
import {AppNavigator, APP_STATES} from "./services/navigator";
import {LoginCard} from "./components/LoginCard";
import {MainPage} from "./pages/MainPage";


function PagerPage() {
    const [curentState, setState] = React.useState(APP_STATES.MAIN_PAGE);

    function APP_STATESCallback(newState: APP_STATES) {
        setState(newState)
    }

    // not ideal re registering every time but it's javascript after all so efficiency is out the window
    AppNavigator.registerListener(APP_STATESCallback, "PagegerPage")

    switch (curentState) {
        case APP_STATES.LOGIN: {
            return <LoginCard/>
        }
        case APP_STATES.MAIN_PAGE: {
            return <MainPage/>
        }

        default: {
            return <text>error</text>
        }
    }

}

function App() {
    return (
        <div className="App">


            <PagerPage/>
        </div>
    );
}

export default App;
