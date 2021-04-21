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
            <Route exact path="/">
                <LoginPage/>
            </Route>

            <Route path={[`/home/`]}>
                <MainPage/>
            </Route>
            {/*<Redirect from="/home/" to="/home/commodity" />*/}
            <Route path={`/home/user/:userID`}>
                <MainPage/>
            </Route>
            {/*<Redirect from="/users/:id" to="/users/profile/:id" />*/}
            {/*<Route path={`/commodity/:commodityid`}>*/}
            {/*    <MainPage/>*/}
            {/*</Route>*/}
            {/*<Route path={`/user/:userid`}>*/}
            {/*    <MainPage/>*/}
            {/*</Route>*/}
        </Switch>
    </Router>

} 



