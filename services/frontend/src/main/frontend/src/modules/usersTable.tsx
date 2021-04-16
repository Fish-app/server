import {Button, List} from "antd";
import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import './../index.css';
import {Table, Tag, Space} from 'antd';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link,
    useParams,
    useRouteMatch
} from "react-router-dom";


import * as apiRequests from '../services/axios_api';
import {AuthUser, Group} from "../services/ApiModels";
import {UserInfoDrawer} from "../pages/UserInfoDrawer";


export function AuthUsersTable() {
    const [userdatathing, setuserdatathing] = useState({authUser: [] as AuthUser[], isLoading: true})

    const [visible, setVisible] = useState(false);

    const [shownUser, setShownUser] = useState(new AuthUser());


    React.useEffect(() => {
        apiRequests.getAllAuthUsers().then(value => setuserdatathing({
            authUser: value as AuthUser[],
            isLoading: false
        }));
    }, []);

    const showDrawer = () => {
        setVisible(true);
    };
    const onClose = () => {
        setVisible(false);
    };


    const columns = [
        {
            title: 'Id',
            dataIndex: 'id',
            key: 'id',
        },
        {
            title: 'Username',
            dataIndex: 'principalName',
            key: 'principalName',
        },
        {
            title: 'Created',
            dataIndex: 'created',
            key: 'created',
        },
        {
            title: '',
            dataIndex: 'id',
            key: 'user_page_id',
            render: (id: number, usr: AuthUser) => <Button type="primary"
                                                           onClick={event => {
                                                               setShownUser(usr);
                                                               setVisible(true)
                                                           }}>Show User Info</Button>
        },

        {
            title: 'User Groups',
            key: 'groups',
            dataIndex: 'groups',
            render: (tags: Group[]) => (
                <>
                    {tags.map(tag => {
                        let color;// = tag.name.length > 5 ? 'geekblue' : 'green';
                        switch (tag.name) {
                            case 'admin':
                                color = 'volcano';
                                break;
                            case 'user':
                                color = 'geekblue';
                                break;
                            case 'seller':
                                color = 'magenta';
                                break;
                            case 'buyer':
                                color = 'lime';
                                break;
                            case 'container':
                                color = 'orange';
                                break;
                            default:
                                color = "geekblue";

                        }

                        let tagV: string = tag.name;
                        return (
                            <Tag color={color} id={tagV}>
                                {tag.name.toUpperCase()}
                            </Tag>
                        );
                    })}
                </>
            ),
        },

    ];


    return <div>
        <Table columns={columns} dataSource={userdatathing.authUser} loading={userdatathing.isLoading}/>
        <UserInfoDrawer user={shownUser} visible={visible} onClose={onClose}/>
    </div>

}


const data = [
    {
        key: '1',
        name: 'John Brown',
        age: 32,
        address: 'New York No. 1 Lake Park',
        tags: ['nice', 'developer'],
    },
    {
        key: '2',
        name: 'Jim Green',
        age: 42,
        address: 'London No. 1 Lake Park',
        tags: ['loser'],
    },
    {
        key: '3',
        name: 'Joe Black',
        age: 32,
        address: 'Sidney No. 1 Lake Park',
        tags: ['cool', 'teacher'],
    },
];