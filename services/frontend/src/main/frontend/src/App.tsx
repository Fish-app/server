import React from 'react';
import logo from './logo.svg';
import './App.css';

import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link,
    useRouteMatch,
    useParams
} from "react-router-dom";
import {TopRouter} from "./services/Router";


function App() {
    return (
        <div className="App">
            <TopRouter/>
        </div>
    );
}

export default App;
