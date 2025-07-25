import styles from '../styles/ManageAddresses.module.css'
import addIcon from '../assets/icons/add.svg'
import { useContext, useEffect, useState } from 'react'
import AddressCard from './AddressCard'
import { useNavigate } from 'react-router'
import AddressesContext from '../contexts/AddressesContext'
import { useAuthFetch } from '../hooks/useAuthFetch'

export default function ManageAddresses({ isSelectMode }) {

    const { addresses, setAddresses } = useContext(AddressesContext)
    const { selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const [addressCards, setAddressCards] = useState([])
    const [threeDotsMenuOpenId, setThreeDotsMenuOpenId] = useState(null)
    const navigate = useNavigate()
    const { authFetch } = useAuthFetch()

    useEffect(() => {
        setAddressCards(addresses.map(address => {
            if (isSelectMode && address.isDefaultShipping)
                setSelectedAddress(address)
            return <AddressCard key={address.addressId} address={address} threeDotsMenuOpenId={threeDotsMenuOpenId} setThreeDotsMenuOpenId={setThreeDotsMenuOpenId} />
        }))
    }, [isSelectMode, addresses, threeDotsMenuOpenId])

    async function getAddresses() {

        const res = await authFetch("http://localhost:8080/api/addresses", {
            method: "GET"
        })
        const addressList = await res.json()
        setAddresses(addressList)
    }

    useEffect(() => {
        const init = async () => { await getAddresses() }
        init()
    }, [])

    return (
        <div className={`${styles.addressesContainer} ${isSelectMode ? styles.selectMode : ''}`} onClick={() => threeDotsMenuOpenId ? setThreeDotsMenuOpenId(null) : undefined}>
            <button className={styles.btnAddAddress} onClick={() => navigate('/addresses/add')}><img src={addIcon} alt="add icon" />Add a New Address</button>
            {...addressCards}
            <button className={styles.btnDeliverAddress} onClick={() => {
                navigate('/checkout')
                }}>Deliver to this address</button>
        </div>
    )
}