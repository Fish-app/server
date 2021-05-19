export class Group {
    public name: string;

    constructor() {
        this.name = " ";
    }
}

export enum UserType {
    Buyer,
    Seller,
    Container,
    Admin
}

export class AuthUser {
    public groups: Group[] | undefined;
    public id: number | undefined;
    public principalName: string | undefined;

    public getUserType(): UserType {
        if (this.groups?.map(value => value.name).lastIndexOf("admin") != -1) {
            return UserType.Admin
        } else if (this.groups?.map(value => value.name).lastIndexOf("container") != -1) {
            return UserType.Container
        } else if (this.groups?.map(value => value.name).lastIndexOf("seller") != -1) {
            return UserType.Seller
        } else if (this.groups?.map(value => value.name).lastIndexOf("buyer") != -1) {
            return UserType.Buyer
        }
        return UserType.Buyer
    }
}

export class CommodityImage {
    public id: number;

    constructor() {
        this.id = -1;
    }


}

export class Commodity {
    public commodityImage: CommodityImage | undefined;
    public id: number | undefined;
    public name: string | undefined;
}
