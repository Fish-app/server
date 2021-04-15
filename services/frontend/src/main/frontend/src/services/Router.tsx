import React, {ReactElement} from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link,
    useParams,
    useRouteMatch
} from "react-router-dom";


export function TopRouter({children}: { children: Array<ReactElement> | ReactElement }): ReactElement {
    return <Router>
        {children}
        <Switch>

        </Switch>
    </Router>

} 



