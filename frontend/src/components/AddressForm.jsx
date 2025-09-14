import { useContext, useEffect, useState } from 'react'
import styles from '../styles/AddressForm.module.css'
import AddressesContext from '../contexts/AddressesContext'
import { useNavigate } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'

export default function AddressForm({ isEditMode }) {

    const navigate = useNavigate()
    const { authFetch } = useAuthFetch()
    const { setAddresses, editingAddress } = useContext(AddressesContext)
    const [address, setAddress] = useState({
        name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME'
    })

    const [errorData, setErrorData] = useState({})

    const validationConfig = {
        name: [{ required: true, message: 'Name is required' }, {minLength: 2, maxLength: 100, message: 'Name must be 2 - 100 character long'}, {pattern: /^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$/, message: "Name can only contain letters, spaces, hyphens (-), apostrophes (') and dots (.)"}],
        mobileNumber: [{ required: true, message: 'Mobile Number is required' }, { pattern: /^[6-9]\d{9}$/, message: 'Please enter a valid 10-digit mobile number' }],
        pincode: [{ required: true, message: 'Pincode is required' }, { pattern: /^[1-9]\d{5}$/, message: 'Please enter a valid 6-digit pincode' }],
        addressLine1: [{ required: true, message: 'Please enter Building/Apartment/Company' }, { minLength: 3, maxLength: 100, message: 'Address must be between 3 - 100 characters long' }, { pattern: /^[A-Za-z0-9\s,./'-]+$/, message: "Only letters, numbers, spaces, and , . / ' - are allowed" }],
        addressLine2: [{ required: true, message: 'Please enter Area/Locality/Street' }, { minLength: 3, maxLength: 100, message: 'Address must be 3 - 100 characters long' }, { pattern: /^[A-Za-z0-9\s,./'-]+$/, message: "Only letters, numbers, spaces, and , . / ' - are allowed" }],
        city: [{ required: true, message: 'City is required' }, { pattern: /^[A-Za-z\s-]{3,100}$/, message: "Please enter a valid city name" }],
        state: [{ required: true, message: 'State is required' }, { pattern: /^[A-Za-z\s-]{3,50}$/, message: "Please enter a valid state name" }]
    }

    useEffect(() => {
        if (isEditMode) {
            if (editingAddress)
                setAddress(editingAddress)
            else
                navigate('/addresses')
        }
        else
            setAddress({
                name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME'
            })
    }, [isEditMode])

    const validateForm = () => {
        const error = {}
        for (const field in validationConfig) {
            validationConfig[field].some(rule => {
                if (rule.required && !address[field]) {
                    error[field] = rule.message
                    return true
                }
                if (rule.minLength && address[field].length < rule.minLength || rule.maxLength && address[field].length > rule.maxLength) {
                    error[field] = rule.message
                    return true
                }
                if (rule.pattern && !rule.pattern.test(address[field])) {
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

    const validateFormField = (field) => {
        const error = { ...errorData }
        validationConfig[field].some(rule => {
            if (rule.required && !address[field]) {
                error[field] = rule.message
                return true
            }
            if (rule.minLength && address[field].length < rule.minLength || rule.maxLength && address[field].length > rule.maxLength) {
                error[field] = rule.message
                return true
            }
            if (rule.pattern && !rule.pattern.test(address[field])) {
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

    async function handleAddAddress() {
        const res = await authFetch(`${apiBaseUrl}/api/addresses`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(address)
        })
        const addressData = await res.json()
        setAddresses(addresses => [...addresses, addressData])
        navigate('../')
    }

    async function handleEditAddress() {
        const res = await authFetch(`${apiBaseUrl}/api/addresses/${editingAddress.addressId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(address)
        })
        const addressData = await res.json()
        setAddresses(addresses => addresses.map(address => {
            if (address.addressId === addressData.addressId)
                return addressData
            return address
        }))
        navigate('../')
    }

    return (
        <form id={styles.addressForm} onSubmit={e => {
            e.preventDefault()
            if (!validateForm())
                return
            isEditMode ? handleEditAddress() : handleAddAddress()
        }}>
            <div>
                <label htmlFor="name">Full name</label>
                <input type="text" id="name" name="name" value={address.name} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, name: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.name}</p>
            </div>
            <div>
                <label htmlFor="mobile-number">Mobile number</label>
                <input type="tel" id="mobile-number" name="mobileNumber" minLength={10} maxLength={10} value={address.mobileNumber} onChange={e => {
                    revokeError()
                    setAddress(address => ({ ...address, mobileNumber: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.mobileNumber}</p>
            </div>
            <div>
                <label htmlFor="pincode">Pincode</label>
                <input type="text" id="pincode" name="pincode" inputMode="numeric" required={true} minLength={6} maxLength={6} value={address.pincode} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, pincode: e.target.value }))
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.pincode}</p>
            </div>
            <div>
                <label htmlFor="address-line-1">Flat, House no., Building, Company, Apartment</label>
                <input type="text" id="address-line-1" name="addressLine1" value={address.addressLine1} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, addressLine1: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.addressLine1}</p>
            </div>
            <div>
                <label htmlFor="address-line-2">Area, Street, Sector, Village</label>
                <input type="text" id="address-line-2" name="addressLine2" value={address.addressLine2} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, addressLine2: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.addressLine2}</p>
            </div>
            <div>
                <label htmlFor="city">City/District/Town</label>
                <input type="text" id="city" name="city" value={address.city} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, city: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.city}</p>
            </div>
            <div>
                <label htmlFor="state">State</label>
                <input type="text" id="state" name="state" value={address.state} onChange={e => {
                    revokeError(e.target.name)
                    setAddress(address => ({ ...address, state: e.target.value }))
                    }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.state}</p>
            </div>
            <div>
                <label htmlFor="address-type">Address Type</label>
                <div className={styles.addressTypes}>
                    <div>
                        <input type="radio" id="home" name="addressType" value="HOME" checked={address.addressType === 'HOME'} onChange={e => setAddress(address => ({ ...address, addressType: e.target.value })) } />
                        <label htmlFor="home">Home</label>
                    </div>
                    <div>
                        <input type="radio" id="work" name="addressType" value="WORK" checked={address.addressType === 'WORK'} onChange={e => setAddress(address => ({ ...address, addressType: e.target.value })) } />
                        <label htmlFor="work">Work</label>
                    </div>
                </div>
            </div>
            <button className={styles.btnAddAddress}>{isEditMode ? 'Edit' : 'Add'} Address</button>
        </form>
    )
}