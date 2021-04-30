import React, {ReactElement} from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Redirect,
    Link,
    useParams,
    useRouteMatch
} from "react-router-dom";
import {LoginPage} from "../pages/LoginPage";
import {MainPage} from "../pages/MainPage";


export function TopRouter(): ReactElement {
    return <Router>
        <Switch>
            <Route exact path="/admin/">
                <LoginPage/>
            </Route>
            <Route path={[`/admin/home/`]}>
                <MainPage/>
            </Route>
            <Route path={`/admin/home/user/:userID`}>
                <MainPage/>
            </Route>
        </Switch>
    </Router>

} 



