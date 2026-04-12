import { useEffect, useReducer, useState } from 'react'
import styles from '../styles/AddressForm.module.css'
import { data, useNavigate, useParams } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { getAddresses } from './ManageAddresses'

async function handleAddAddress(authFetch, newAddress) {
    const res = await authFetch(`${apiBaseUrl}/api/addresses`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newAddress)
    })
    return await res.json()
}

async function handleEditAddress(authFetch, updatedAddress) {
    const res = await authFetch(`${apiBaseUrl}/api/addresses/${editingAddress.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedAddress)
    })
    return await res.json()
}

const addressFormReducer = (state, action) => {
    const { type, payload } = action
    switch (type) {
        case 'NAME':
            return { ...state, 'NAME': payload }
        case 'MOBILE_NUMBER':
            return { ...state, 'PINCODE': payload }
        case 'ADDRESS_LINE_1':
            return { ...state, 'ADDRESS_LINE_1': payload }
        case 'ADDRESS_LINE_2':
            return { ...state, 'ADDRESS_LINE_2': payload }
        case 'CITY':
            return { ...state, 'CITY': payload }
        case 'STATE':
            return { ...state, 'STATE': payload }
        case 'ADDRESS_TYPE':
            return { ...state, 'ADDRESS_TYPE': payload }
        default:
            return { 'NAME': payload.name, 'PINCODE': payload.pincode, 'ADDRESS_LINE_1': payload.addressLine1, 'ADDRESS_LINE_2': payload.addressLine2, 'CITY': payload.city, 'STATE': payload.state, 'ADDRESS_TYPE': payload.addressType }
    }
}

export default function AddressForm() {

    const navigate = useNavigate()
    const authFetch = useAuthFetch()
    const { addressId } = useParams()

    const [addressForm, dispatchAddress] = useReducer(addressFormReducer, { name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME' })

    const { data: address, isSuccess, isError, error } = useQuery({
        queryKey: ['addresses', 'list', addressId],
        queryFn: () => getAddresses(authFetch),
        enabled: !!addressId,
        select: (list) => list.find((a) => a.id === addressId),
        staleTime: 1000 * 60 * 5
    })

    useEffect(() => {
        if (isSuccess && data)
            dispatchAddress(address)
    }, [isSuccess])

    const [errorData, setErrorData] = useState({})

    const validationConfig = {
        name: [{ required: true, message: 'Name is required' }, { minLength: 2, maxLength: 100, message: 'Name must be 2 - 100 character long' }, { pattern: /^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$/, message: "Name can only contain letters, spaces, hyphens (-), apostrophes (') and dots (.)" }],
        mobileNumber: [{ required: true, message: 'Mobile Number is required' }, { pattern: /^[6-9]\d{9}$/, message: 'Please enter a valid 10-digit mobile number' }],
        pincode: [{ required: true, message: 'Pincode is required' }, { pattern: /^[1-9]\d{5}$/, message: 'Please enter a valid 6-digit pincode' }],
        addressLine1: [{ required: true, message: 'Please enter Building/Apartment/Company' }, { minLength: 3, maxLength: 100, message: 'Address must be between 3 - 100 characters long' }, { pattern: /^[A-Za-z0-9\s,./'-]+$/, message: "Only letters, numbers, spaces, and , . / ' - are allowed" }],
        addressLine2: [{ required: true, message: 'Please enter Area/Locality/Street' }, { minLength: 3, maxLength: 100, message: 'Address must be 3 - 100 characters long' }, { pattern: /^[A-Za-z0-9\s,./'-]+$/, message: "Only letters, numbers, spaces, and , . / ' - are allowed" }],
        city: [{ required: true, message: 'City is required' }, { pattern: /^[A-Za-z\s-]{3,100}$/, message: "Please enter a valid city name" }],
        state: [{ required: true, message: 'State is required' }, { pattern: /^[A-Za-z\s-]{3,50}$/, message: "Please enter a valid state name" }]
    }

    const validateForm = () => {
        const error = {}
        for (const field in validationConfig) {
            validationConfig[field].some(rule => {
                if (rule.required && !addressForm[field]) {
                    error[field] = rule.message
                    return true
                }
                if (rule.minLength && addressForm[field].length < rule.minLength || rule.maxLength && addressForm[field].length > rule.maxLength) {
                    error[field] = rule.message
                    return true
                }
                if (rule.pattern && !rule.pattern.test(addressForm[field])) {
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
            if (rule.required && !addressForm[field]) {
                error[field] = rule.message
                return true
            }
            if (rule.minLength && addressForm[field].length < rule.minLength || rule.maxLength && addressForm[field].length > rule.maxLength) {
                error[field] = rule.message
                return true
            }
            if (rule.pattern && !rule.pattern.test(addressForm[field])) {
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

    const queryClient = useQueryClient()

    const { mutate: addAddress } = useMutation({
        mutationFn: (address) => handleAddAddress(authFetch, address),
        onSuccess: (addedAddress) => {
            queryClient.setQueryData(['addresses', 'list', addedAddress.id], addedAddress)
            navigate('../')
        }
    })

    const { mutate: editAddress } = useMutation({
        mutationFn: (address) => handleEditAddress(authFetch, address),
        onSuccess: (updatedAddress) => {
            queryClient.setQueryData(['addresses', 'list', addressId], updatedAddress)
            navigate('../')
        }
    })

    return (
        <form id={styles.addressForm} onSubmit={e => {
            e.preventDefault()
            if (!validateForm())
                return
            addressId ? editAddress(authFetch, addressForm) : addAddress(authFetch, addressForm)
        }}>
            <div>
                <label htmlFor="name">Full name</label>
                <input type="text" id="name" name="name" autoComplete='name' value={addressForm.name} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'NAME', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.name}</p>
            </div>
            <div>
                <label htmlFor="mobile-number">Mobile number</label>
                <input type="tel" id="mobile-number" name="mobileNumber" minLength={10} maxLength={10} value={addressForm.mobileNumber} onChange={e => {
                    revokeError()
                    dispatchAddress({ type: 'MOBILE_NUMBER', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.mobileNumber}</p>
            </div>
            <div>
                <label htmlFor="pincode">Pincode</label>
                <input type="text" id="pincode" name="pincode" inputMode="numeric" required={true} minLength={6} maxLength={6} value={addressForm.pincode} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'PINCODE', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.pincode}</p>
            </div>
            <div>
                <label htmlFor="address-line-1">Flat, House no., Building, Company, Apartment</label>
                <input type="text" id="address-line-1" name="addressLine1" value={addressForm.addressLine1} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'ADDRESS_LINE_1', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.addressLine1}</p>
            </div>
            <div>
                <label htmlFor="address-line-2">Area, Street, Sector, Village</label>
                <input type="text" id="address-line-2" name="addressLine2" value={addressForm.addressLine2} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'ADDRESS_LINE_2', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.addressLine2}</p>
            </div>
            <div>
                <label htmlFor="city">City/District/Town</label>
                <input type="text" id="city" name="city" value={addressForm.city} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'CITY', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.city}</p>
            </div>
            <div>
                <label htmlFor="state">State</label>
                <input type="text" id="state" name="state" value={addressForm.state} onChange={e => {
                    revokeError(e.target.name)
                    dispatchAddress({ type: 'STATE', payload: e.target.value })
                }} onBlur={e => validateFormField(e.target.name)} />
                <p className={styles.error}>{errorData.state}</p>
            </div>
            <div>
                <div>Address Type</div>
                <div className={styles.addressTypes}>
                    <div>
                        <input type="radio" id="home" name="addressType" value="HOME" checked={addressForm.addressType === 'HOME'} onChange={e => dispatchAddress({ type: 'ADDRESS_TYPE', payload: e.target.value })} />
                        <label htmlFor="home">Home</label>
                    </div>
                    <div>
                        <input type="radio" id="work" name="addressType" value="WORK" checked={addressForm.addressType === 'WORK'} onChange={e => dispatchAddress({ type: 'ADDRESS_TYPE', payload: e.target.value })} />
                        <label htmlFor="work">Work</label>
                    </div>
                </div>
            </div>
            <button className={styles.btnAddAddress}>{addressId ? 'Edit' : 'Add'} Address</button>
        </form>
    )
}