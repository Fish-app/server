import axios from "axios";
import {Session} from "inspector";
import {AppSession} from "./Session";

const apiConfig = {
    // `url` is the server URL that will be used for the request
    // url: '/user',

    // `method` is the request method to be used when making the request
    // method: 'get', // default

    // `baseURL` will be prepended to `url` unless `url` is absolute.
    // It can be convenient to set `baseURL` for an instance of axios to pass relative URLs
    // to methods of that instance.
    baseURL: 'http://localhost:80/api',


    // `headers` are custom headers to be sent
    // headers: {'X-Requested-With': 'XMLHttpRequest'},

    // `params` are the URL parameters to be sent with the request
    // Must be a plain object or a URLSearchParams object
    // params: {
    //     ID: 12345
    // },


    // `responseType` indicates the type of data that the server will respond with
    // options are: 'arraybuffer', 'document', 'json', 'text', 'stream'
    //   browser only: 'blob'
    // responseType: 'json', // default

    // `responseEncoding` indicates encoding to use for decoding responses (Node.js only)
    // Note: Ignored for `responseType` of 'stream' or client-side requests
    // responseEncoding: 'utf8', // default


}

export function login(username: string, password: string) {
    let a = axios({
            ...apiConfig,
            url: '/auth/authentication/login',
            method: 'post',
            data: {userName: username, password: password}
        }
    )


    a.then(value => console.log(value))

   
    return a
}

export function getCurrentUser() {
    let a = axios({
            ...apiConfig,
            url: '/user/seller/current',
            method: 'get',
            headers: {authorization: AppSession.isLoggedIn() ? AppSession.getToken() : null}
        }
    )

    a.then(value => console.log(value))
    return a

}
