import { useContext, useEffect, useState } from 'react'
import styles from '../styles/AddressForm.module.css'
import AddressesContext from '../contexts/AddressesContext'
import { useNavigate } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'

export default function AddAddress({ isEditMode }) {

    const navigate = useNavigate()
    const authFetch = useAuthFetch()
    const { setAddresses, editingAddress } = useContext(AddressesContext)
    const [address, setAddress] = useState({
        name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME'
    })

    useEffect(() => {
        if (isEditMode)
            setAddress(editingAddress)
        else
            setAddress({
                name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME'
            })
    }, [isEditMode])

    async function handleAddAddress() {
        const res = await authFetch('http://localhost:8080/api/addresses', {
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
        const res = await authFetch(`http://localhost:8080/api/addresses/${editingAddress.addressId}`, {
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
            isEditMode ? handleEditAddress() : handleAddAddress()
        }}>
            <div>
                <label htmlFor="name">Full name</label>
                <input type="text" id="name" name="name" value={address.name} onChange={e => setAddress(address => { return { ...address, name: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="mobile-number">Mobile number</label>
                <input type="tel" id="mobile-number" name="mobileNumber" minLength={10} maxLength={10} value={address.mobileNumber} onChange={e => setAddress(address => { return { ...address, mobileNumber: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="pincode">Pincode</label>
                <input type="text" id="pincode" name="pincode" inputMode="numeric" required={true} minLength={6} maxLength={6} value={address.pincode} onChange={e => setAddress(address => { return { ...address, pincode: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="address-line-1">Flat, House no., Building, Company, Apartment</label>
                <input type="text" id="address-line-1" name="addressLine1" value={address.addressLine1} onChange={e => setAddress(address => { return { ...address, addressLine1: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="address-line-2">Area, Street, Sector, Village</label>
                <input type="text" id="address-line-2" name="addressLine2" value={address.addressLine2} onChange={e => setAddress(address => { return { ...address, addressLine2: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="city">City/District/Town</label>
                <input type="text" id="city" name="city" value={address.city} onChange={e => setAddress(address => { return { ...address, city: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="state">State</label>
                <input type="text" id="state" name="state" value={address.state} onChange={e => setAddress(address => { return { ...address, state: e.target.value } })} />
            </div>
            <div>
                <label htmlFor="address-type">Address Type</label>
                <input type="radio" id="home" name="addressType" value="HOME" checked={address.addressType === 'HOME'} onChange={e => setAddress(address => { return { ...address, addressType: e.target.value } })} />
                <label htmlFor="home">Home</label>
                <input type="radio" id="work" name="addressType" value="WORK" checked={address.addressType === 'WORK'} onChange={e => setAddress(address => { return { ...address, addressType: e.target.value } })} />
                <label htmlFor="work">Work</label>
            </div>
            <button className={styles.btnAddAddress}>{isEditMode ? 'Edit' : 'Add'} Address</button>
        </form>
    )
}