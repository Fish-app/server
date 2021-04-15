class AppSessionC {

    private jwtToken?: string;


    constructor() {
        this.jwtToken = undefined;
    }

    public isLoggedIn() {
        return this.jwtToken !== undefined;
    }

    public invalidateToken() {
        this.jwtToken = undefined;
    }

    public setToken(token: string) {
        this.jwtToken = token;
    }

    public getToken() {
        return this.jwtToken;
    }
}

export const AppSession = new AppSessionC();