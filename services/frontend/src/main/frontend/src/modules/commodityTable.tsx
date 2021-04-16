import {List} from "antd";
import React, {useState} from 'react';
import ReactDOM from 'react-dom';
import 'antd/dist/antd.css';
import './../index.css';
import {Table, Tag, Space} from 'antd';

import * as apiRequests from '../services/axios_api';
import {Commodity, CommodityImage} from "../services/ApiModels";


export function CommodityTable() {
    const [commodityData, setCommodityData] = useState({commoditys: [] as Commodity[], isLoading: true})

    React.useEffect(() => {
        apiRequests.getAllCommoditys().then(value => setCommodityData({
            commoditys: value as Commodity[],
            isLoading: false
        }));
    }, []);


    return <Table columns={columns} dataSource={commodityData.commoditys} loading={commodityData.isLoading}/>

}


const columns = [
    {
        title: 'Id',
        dataIndex: 'id',
        key: 'id',
    },
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        render: (text: string) => <a>{text}</a>,
    },

    {
        title: 'image',
        dataIndex: 'commodityImage',
        key: 'commodityImage',
        render: (imageData: CommodityImage) => <img
            src={apiRequests.IMAGE_URL + imageData.id + "?width=500"}
            alt="new"
            id={imageData.id.toString()}/>,
    },

];

