import { useContext, useEffect, useState } from "react";
import styles from "../styles/Profile.module.css";
import UserContext from "../contexts/UserContext";
import { Loader2 } from "lucide-react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { api } from "@/api/client";

async function apiUpdateProfile(profileForm) {
    const profileFormData = new FormData(profileForm)
    return api.put(`/users`, Object.fromEntries(profileFormData.entries()))
}

export default function Profile() {

    const { user } = useContext(UserContext)
    const [modifiedUserInfo, setModifiedUserInfo] = useState({})
    const [errorData, setErrorData] = useState({})
    const validationConfig = {
        mobileNumber: [{ required: true, message: 'Mobile number cannot be empty' }, { pattern: /^[6789]{1}[0-9]{9}$/, message: 'Please enter a valid 10-digit mobile number' }],
        email: [{ pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: 'Invalid email' }],
        firstName: [{ minLength: 2, message: 'First name must contain at least 2 characters' }, { maxLength: 50, message: 'First name can be at most 50 characters long' }, { pattern: /^[A-Za-z]*$/, message: 'First name must contain only letters' }],
        lastName: [{ minLength: 2, message: 'Last name must contain at least 2 characters' }, { maxLength: 100, message: 'Last name must be at most 100 characters long' }, { pattern: /^[A-Za-z]+(?:\\s[A-Za-z]+)*$/, message: 'Last name must contain only letters or spaces' }]
    }

    useEffect(() => {
        if (user) {
            setModifiedUserInfo(user)
        }
    }, [user])

    useEffect(() => {
        const unmodified = Object.keys(modifiedUserInfo).every(key => modifiedUserInfo[key] === user[key])
        if (unmodified)
            setFormModified(false)
    }, [modifiedUserInfo])

    const [isFormModified, setFormModified] = useState(false)

    const queryClient = useQueryClient()

    const { mutate: updateProfile } = useMutation({
        mutationFn: (profile) => apiUpdateProfile(profile),
        onSuccess: (updatedUser) => { 
            queryClient.setQueryData(['user'], updatedUser)
            toast.success('Profile updated successfully')
        }
    })

    function validateForm() {
        const error = {}
        for (const field in modifiedUserInfo) {
            validationConfig[field]?.some(rule => {
                if (rule.required && !modifiedUserInfo[field]) {
                    error[field] = rule.message
                    return true
                }
                if (rule.minLength && modifiedUserInfo[field] && modifiedUserInfo[field].length < rule.minLength || rule.maxLength && modifiedUserInfo[field].length > rule.maxLength) {
                    error[field] = rule.message
                    return true
                }
                if (rule.pattern && modifiedUserInfo[field] && !rule.pattern.test(modifiedUserInfo[field])) {
                    error[field] = rule.message
                    return true
                }
            })
        }

        if (Object.keys(error).length)
            setErrorData(error)
        else
            return true
    }

    function validateFormField(field) {
        const error = { ...errorData }
        validationConfig[field]?.some(rule => {
            if (rule.required && !modifiedUserInfo[field]) {
                error[field] = rule.message
                return true
            }
            if (rule.minLength && modifiedUserInfo[field] && modifiedUserInfo[field].length < rule.minLength || rule.maxLength && modifiedUserInfo[field].length > rule.maxLength) {
                error[field] = rule.message
                return true
            }
            if (rule.pattern && modifiedUserInfo[field] && !rule.pattern.test(modifiedUserInfo[field])) {
                error[field] = rule.message
                return true
            }
        })
        if (error[field])
            setErrorData(error)
    }

    function revokeError(field) {
        if (errorData[field]) {
            setErrorData(errorData => {
                delete errorData[field]
                return { ...errorData }
            })
        }
    }

    return Object.keys(modifiedUserInfo).length ? (
        <form className={isFormModified ? styles.changed : ""} id={styles.profileForm} onSubmit={e => {
            e.preventDefault()
            if (!validateForm())
                return
            updateProfile(e.currentTarget)
            setFormModified(false)
        }}>
            <h1 className={styles.profileInfoTitle}>Profile Information</h1>
            <div className={styles.name}>
                <div>
                    <label htmlFor="firstName">First name</label>
                    <input type="text" id="firstName" name="firstName" value={modifiedUserInfo.firstName} onChange={e => {
                        setModifiedUserInfo({ ...modifiedUserInfo, firstName: e.target.value })
                        revokeError(e.target.name)
                        if (e.target.value !== user.firstName)
                            setFormModified(true)
                    }} onBlur={e => validateFormField(e.target.name)} />
                    <div className={styles.error}>{errorData.firstName}</div>
                </div>
                <div>
                    <label htmlFor="lastName">Last name</label>
                    <input type="text" id="lastName" name="lastName" value={modifiedUserInfo.lastName} onChange={e => {
                        setModifiedUserInfo({ ...modifiedUserInfo, lastName: e.target.value })
                        revokeError(e.target.name)
                        if (e.target.value !== user.lastName)
                            setFormModified(true)
                    }} onBlur={e => validateFormField(e.target.name)} />
                    <div className={styles.error}>{errorData.lastName}</div>
                </div>
            </div>
            <div>
                <label htmlFor="email">Email Address</label>
                <input type="email" id="email" name="email" autoComplete="email" value={modifiedUserInfo.email} onChange={e => {
                    setModifiedUserInfo({ ...modifiedUserInfo, email: e.target.value })
                    revokeError(e.target.name)
                    if (e.target.value !== user.email)
                        setFormModified(true)
                }} onBlur={e => validateFormField(e.target.name)} />
                <div className={styles.error}>{errorData.email}</div>
            </div>
            <button className={styles.btnSaveChanges}>Save changes</button>
        </form>
    ) : <Loader2 className="animate-spin" />
}