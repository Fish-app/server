import React, {ReactElement, useState} from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import '../index.scss';
import {Breadcrumb, Layout, Menu} from 'antd';
import {UploadOutlined, UserOutlined, VideoCameraOutlined} from '@ant-design/icons';
import {AuthUsersTable} from "../modules/usersTable";
import {CommodityTable} from "../modules/commodityTable";

const {Header, Content, Footer, Sider} = Layout;

enum HomPageCategories {
    Home,
    Commodity,
    User,
    Listings
}

const HomePageContent = HomPageCategories.Home


function Abcccc(props: { currentCat: HomPageCategories }): ReactElement {
    switch (props.currentCat) {
        case HomPageCategories.Home:
            return <h1>home</h1>
        case HomPageCategories.User:
            return <AuthUsersTable/>
        case HomPageCategories.Commodity:
            return <CommodityTable/>
        default:
            return <h1>not found</h1>
    }
}

export function MainPage() {
    const [currentIndex, setIndex] = useState(HomePageContent)


    return <Layout className="layout">
        <Header>
            <div className="logo">Fishapp</div>
            <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['1']}>
                <Menu.Item key="1" onClick={info => setIndex(HomPageCategories.Home)}>nav 1</Menu.Item>
                <Menu.Item key="2" onClick={info => setIndex(HomPageCategories.User)}>nav 2</Menu.Item>
                <Menu.Item key="3" onClick={info => setIndex(HomPageCategories.Commodity)}>nav 3</Menu.Item>
            </Menu>
        </Header>
        <Content style={{padding: '0 50px'}}>
            <Breadcrumb style={{margin: '16px 0'}}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>List</Breadcrumb.Item>
                <Breadcrumb.Item>App</Breadcrumb.Item>
            </Breadcrumb>
            <div className="site-layout-content"><Abcccc currentCat={currentIndex}/></div>

        </Content>
        <Footer style={{textAlign: 'center'}}>Ant Design ©2018 Created by Ant UED</Footer>
    </Layout>


    // <Layout>
    //     <Sider
    //         breakpoint="lg"
    //         collapsedWidth="0"
    //         onBreakpoint={broken => {
    //             console.log(broken);
    //         }}
    //         onCollapse={(collapsed, type) => {
    //             console.log(collapsed, type);
    //         }}
    //     >
    //         <div className="logo">
    //             <h2>Fishapp</h2>
    //         </div>
    //         <Menu theme="dark" mode="inline" defaultSelectedKeys={['4']}>
    //             <Menu.Item key="1" icon={<UserOutlined/>}>
    //                 nav 1
    //             </Menu.Item>
    //             <Menu.Item key="2" icon={<VideoCameraOutlined/>}>
    //                 nav 2
    //             </Menu.Item>
    //             <Menu.Item key="3" icon={<UploadOutlined/>}>
    //                 nav 3
    //             </Menu.Item>
    //             <Menu.Item key="4" icon={<UserOutlined/>}>
    //                 nav 4
    //             </Menu.Item>
    //         </Menu>
    //     </Sider>
    //     <Layout>
    //         <Header className="site-layout-sub-header-background" style={{padding: 0}}/>
    //         <Content style={{margin: '24px 16px 0'}}>
    //             <div className="site-layout-background" style={{padding: 24, minHeight: 360}}>
    //                 content
    //             </div>
    //         </Content>
    //         <Footer style={{textAlign: 'center'}}>XXXX FILL ME IN LATER XXXX</Footer>
    //     </Layout>
    // </Layout>

}