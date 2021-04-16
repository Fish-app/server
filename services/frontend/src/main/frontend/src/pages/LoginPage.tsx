import React from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import * as apiRequests from '../services/axios_api';
import {Form, Input, Button, Checkbox} from 'antd';
import { Redirect, Route, RouteComponentProps, RouteProps , useHistory} from 'react-router-dom'
import axios from "axios";
import {AuthHandler} from "../services/AuthHandler";


const layout = {
    labelCol: {
        span: 8,
    },
    wrapperCol: {
        span: 16,
    },
};
const tailLayout = {
    wrapperCol: {
        offset: 8,
        span: 16,
    },
};


export function LoginPage() {

    const history = useHistory();

    const onFinish = async (values: any) => {
        let resp = await apiRequests.login(values.userName, values.password)
        let auth = resp.headers.authorization
        console.log(auth)
        AuthHandler.setToken(auth)

        //let aaa = await apiRequests.getCurrentUser();
        //console.log(aaa)

        history.push("/home")

        console.log('Success:', values);
    };

    const onFinishFailed = (errorInfo: any) => {
        console.log('Failed:', errorInfo);
    };

    return (
        <Form
            {...layout}
            name="basic"
            initialValues={{
                remember: true,
            }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
        >
            <Form.Item
                label="Username"
                name="userName"
                rules={[
                    {
                        required: true,
                        message: 'Please input your username!',
                    },
                ]}
            >
                <Input/>
            </Form.Item>

            <Form.Item
                label="Password"
                name="password"
                rules={[
                    {
                        required: true,
                        message: 'Please input your password!',
                    },
                ]}
            >
                <Input.Password/>
            </Form.Item>

            <Form.Item {...tailLayout} name="remember" valuePropName="checked">
                <Checkbox>Remember me</Checkbox>
            </Form.Item>

            <Form.Item {...tailLayout}>
                <Button type="primary" htmlType="submit">
                    Submit
                </Button>
            </Form.Item>
        </Form>
    );
}
