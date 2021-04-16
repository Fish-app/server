import {List} from "antd";
import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import './../index.css';
import {Table, Tag, Space} from 'antd';

import * as apiRequests from '../services/axios_api';


class Group {
    public name: string;

    constructor() {
        this.name = " ";
    }
}

class AuthUser {
    public groups: Group[] | undefined;
    public id: number | undefined;
    public principalName: string | undefined;
}


export function AuthUsersTable() {
    const [userdatathing, setuserdatathing] = useState({authUser: [] as AuthUser[], isLoading: true})

    React.useEffect(() => {
        apiRequests.getAllAuthUsers().then(value => setuserdatathing({
            authUser: value as AuthUser[],
            isLoading: false
        }));
    }, []);


    return <Table columns={columns} dataSource={userdatathing.authUser} loading={userdatathing.isLoading}/>

}


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
        render: (text: string) => <a>{text}</a>,
    },
    {
        title: 'Created',
        dataIndex: 'created',
        key: 'created',
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
    // {
    //     title: 'Action',
    //     key: 'action',
    //     render: (text: string, record: ()) => (
    //         <Space size="middle">
    //             <a>Invite {record.name}</a>
    //             <a>Delete</a>
    //         </Space>
    //     ),
    // },
];

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