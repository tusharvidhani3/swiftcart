import styles from '../styles/ManageAddresses.module.css'
import { useContext, useEffect, useState } from 'react'
import AddressCard from './AddressCard'
import { useNavigate } from 'react-router'
import AddressesContext from '../contexts/AddressesContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { Loader2, Plus } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'

export async function getAddresses(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/addresses`, {
        method: "GET"
    })
    return await res.json()
}

export default function ManageAddresses({ isSelectMode, setShowAddressSelector }) {

    const { setSelectedAddress } = useContext(AddressesContext)
    const [addressCards, setAddressCards] = useState(null)
    const [threeDotsMenuOpenId, setThreeDotsMenuOpenId] = useState(null)
    const navigate = useNavigate()
    const authFetch = useAuthFetch()

    const { data: addresses, isLoading } = useQuery({
        queryKey: ['addresses', 'list'],
        queryFn: () => getAddresses(authFetch),
        staleTime: 1000 * 60 * 5
    })

    useEffect(() => {
        setAddressCards(addresses?.map(address => {
            if (isSelectMode && address.defaultShipping)
                setSelectedAddress(address)
            return <AddressCard key={address.id} address={address} threeDotsMenuOpenId={threeDotsMenuOpenId} setThreeDotsMenuOpenId={setThreeDotsMenuOpenId} setShowAddressSelector={setShowAddressSelector} />
        }))
    }, [isSelectMode, addresses, threeDotsMenuOpenId])

    return addressCards ? (
        <div className={`${styles.addressesContainer} ${isSelectMode ? styles.selectMode : ''}`} onClick={() => threeDotsMenuOpenId ? setThreeDotsMenuOpenId(null) : undefined}>
            <button className={styles.btnAddAddress} onClick={() => navigate('/addresses/add')}><Plus />Add a New Address</button>
            {...addressCards}
        </div>
    ) : <Loader2 className='animate-spin' />
}