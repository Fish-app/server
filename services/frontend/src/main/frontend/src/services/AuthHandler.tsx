enum LoginLevel {
    loggedIn,
    loggedOut
}

class AuthHandlerC {
    private loginLevel: LoginLevel;
    private jwtToken?: string;

    constructor() {
        this.loginLevel = LoginLevel.loggedOut;
    }

    public isLoggedIn(): boolean {
        return this.loginLevel === LoginLevel.loggedIn;
    }

    public setLoginLevel(level: LoginLevel) {
        this.loginLevel = level;
    }


    public invalidateToken() {
        this.jwtToken = undefined;
        this.loginLevel = LoginLevel.loggedOut;

    }

    public setToken(token: string) {
        this.jwtToken = token;
        this.loginLevel = LoginLevel.loggedIn;
    }

    public getToken(): string | undefined {
        return this.jwtToken;
    }
}


export const AuthHandler = new AuthHandlerC();