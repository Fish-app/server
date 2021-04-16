import React, {ReactElement} from "react";
import {Button, Drawer} from "antd";
import {AuthUser, UserType} from "../services/ApiModels";


export function UserInfoDrawer(props: { user: AuthUser, visible: boolean, onClose: () => void }): ReactElement {
    const authUser = Object.assign(new AuthUser(), props.user);
    const userType = authUser.getUserType();

    // React.useEffect(() => {
    //     apiRequests.getAllAuthUsers().then(value => setuserdatathing({
    //         authUser: value as AuthUser[],
    //         isLoading: false
    //     }));
    // }, []);

    function BuildDrawer(): ReactElement {
        switch (userType) {
            case UserType.Admin:
            case UserType.Container:
                return <h3>Cant remove admin/container users</h3>
            case UserType.Buyer:
                return <div>
                    <Button>Delete Buyer</Button>
                </div>
            case UserType.Seller:
                return <div>
                    <Button>Delete Seller</Button>
                </div>
            default:
                return <h3>error</h3>
        }
        return <h1>loading</h1>
    }


    return <Drawer
        title={"User: " + props.user.principalName}
        placement="right"
        closable={false}
        onClose={props.onClose}
        visible={props.visible}
    >
        <BuildDrawer/>
    </Drawer>
}