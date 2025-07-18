import styles from '../styles/ManageAddresses.module.css'
import addIcon from '../assets/icons/add.svg'
import { useEffect, useState } from 'react'
import AddressCard from './AddressCard'
import { useLocation, useNavigate } from 'react-router'

export default function ManageAddresses() {

    const [addresses, setAddresses] = useState([])
    const navigate = useNavigate()
    const location = useLocation()
    const isSelectMode = location.state
    async function getAddresses() {

        const res = await fetch("http://localhost:8080/api/addresses", {
            method: "GET",
            credentials: "include",
        })
        const addressList = await res.json()
        setAddresses(addressList)
    }

    useEffect(() => {
        const init = async () => { await getAddresses() }
        init()
    }, [])

    return (
        <div className={`${styles.addressesContainer} ${isSelectMode?styles.selectMode:''}`}>
            <button className={styles.btnAddAddress} onClick={() => navigate('/add-address')}><img src={addIcon} alt="add icon" />Add a New Address</button>
            {addresses.map(address => <AddressCard {...address} />)}
            <button className={styles.btnDeliverAddress}>Deliver to this address</button>
        </div>
    )
}