import { useContext, useEffect, useState } from "react";
import styles from "../styles/Profile.module.css";
import UserContext from "../contexts/UserContext";
import { useAuthFetch } from "../hooks/useAuthFetch";
import { apiBaseUrl } from "../config";

export default function Profile() {

    const { userInfo, setUserInfo } = useContext(UserContext)
    const [modifiedUserInfo, setModifiedUserInfo] = useState(null)
    const { authFetch } = useAuthFetch()

    useEffect(() => {
        if (userInfo) {
            setModifiedUserInfo(userInfo)
        }
    }, [userInfo])

    const [isFormModified, setFormModified] = useState(false)

    async function updateProfile(profileForm) {
        const profileFormData = new FormData(profileForm)
        const res = await authFetch(`${apiBaseUrl}/api/users`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(Object.fromEntries(profileFormData.entries()))
        })
        const updatedUserInfo = await res.json()
        setUserInfo(updatedUserInfo)
    }

    return (
        <form className={isFormModified ? styles.changed : ""} id={styles.profileForm} onSubmit={e => {
            e.preventDefault()
            updateProfile(e.currentTarget)
            setFormModified(false)
        }}>
            <h1 className={styles.profileInfoTitle}>Profile Information</h1>
            <div className={styles.name}>
                <div>
                    <label htmlFor="firstName">First name</label>
                    <input type="text" id="firstName" name="firstName" value={modifiedUserInfo.firstName} onChange={e => {
                        setModifiedUserInfo({ ...modifiedUserInfo, firstName: e.target.value })
                        if (e.target.value !== userInfo.firstName)
                            setFormModified(true)
                        else
                            setFormModified(false)
                    }} />
                </div>
                <div>
                    <label htmlFor="lastName">Last name</label>
                    <input type="text" id="lastName" name="lastName" value={modifiedUserInfo.lastName} onChange={e => {
                        setModifiedUserInfo({ ...modifiedUserInfo, lastName: e.target.value })
                        if (e.target.value !== userInfo.lastName)
                            setFormModified(true)
                        else
                            setFormModified(false)
                    }} />
                </div>
            </div>
            <div>
                <label htmlFor="mobileNumber">Mobile number</label>
                <input type="tel" id="mobileNumber" name="mobileNumber" required value={modifiedUserInfo.mobileNumber} onChange={e => {
                    setModifiedUserInfo({ ...modifiedUserInfo, mobileNumber: e.target.value })
                    if (e.target.value !== userInfo.mobileNumber)
                        setFormModified(true)
                    else
                        setFormModified(false)
                }} />
            </div>
            <div>
                <label htmlFor="email">Email Address</label>
                <input type="email" id="email" name="email" value={modifiedUserInfo.email} onChange={e => {
                    setModifiedUserInfo({ ...modifiedUserInfo, email: e.target.value })
                    if (e.target.value !== userInfo.email)
                        setFormModified(true)
                    else
                        setFormModified(false)
                }} />
            </div>
            <button className={styles.btnSaveChanges}>Save changes</button>
        </form>
    )
}