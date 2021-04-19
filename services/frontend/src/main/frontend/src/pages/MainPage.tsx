import React, {ReactElement, useState} from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import '../index.scss';
import {Breadcrumb, Layout, Menu} from 'antd';
import {UploadOutlined, UserOutlined, VideoCameraOutlined} from '@ant-design/icons';
import {AuthUsersTable} from "../modules/usersTable";
import {CommodityTable} from "../modules/commodityTable";
import {Redirect, Route, Switch, useHistory, useRouteMatch, Link, useParams} from "react-router-dom";

const {Header, Content, Footer, Sider} = Layout;

enum HomPageCategories {
    Home = 1,
    User = 2,
    Commodity = 3,
    Listings = 4
}

function getPathAsEnum(path: string): HomPageCategories {
    if (path == "home") {
        return HomPageCategories.Home
    } else if (path == "user") {
        return HomPageCategories.User
    } else if (path == "commodity") {
        return HomPageCategories.Commodity
    } else if (path == "home") {
        return HomPageCategories.Listings
    } else {
        return HomPageCategories.Home
    }
}

const HomePageContent = HomPageCategories.Home

//
function Abcccc(props: { setIndex: (abc: HomPageCategories) => any }): ReactElement {
    let {home_spot} = useParams<{ home_spot: string }>();

    switch (getPathAsEnum(home_spot)) {
        case HomPageCategories.Home:
            props.setIndex(HomPageCategories.Home)
            return <h1>home</h1>
        case HomPageCategories.User:
            props.setIndex(HomPageCategories.User)
            return <AuthUsersTable/>
        case HomPageCategories.Commodity:
            props.setIndex(HomPageCategories.Commodity)
            return <CommodityTable/>
        default:
            return <h1>not found</h1>
    }
}

export function MainPage() {
    const [currentIndex, setIndex] = useState(HomPageCategories.Home)

    let {path, url} = useRouteMatch();
    let history = useHistory();

    console.log(path)
    console.log(url)
    return <Layout className="layout">
        <Header>
            <div className="logo">Fishapp</div>
            <Menu theme="dark" mode="horizontal" selectedKeys={[currentIndex.toString()]}>
                <Menu.Item key="1" onClick={info => history.push(`${url}/home`)}>Home</Menu.Item>
                <Menu.Item key="2" onClick={info => history.push(`${url}/user`)}>User</Menu.Item>
                <Menu.Item key="3" onClick={info => history.push(`${url}/commodity`)}>Commodity</Menu.Item>
            </Menu>
        </Header>
        <Content style={{padding: '0 50px'}}>
            <Breadcrumb style={{margin: '16px 0'}}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>List</Breadcrumb.Item>
                <Breadcrumb.Item>App</Breadcrumb.Item>
            </Breadcrumb>
            <div className="site-layout-content">

                <Switch>
                    <Route path={`/home/:home_spot`}>
                        <Abcccc setIndex={setIndex}/>
                    </Route>
                </Switch>
            </div>
        </Content>
    </Layout>
}