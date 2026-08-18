import { useNavigate } from 'react-router'
import { useQuery } from '@tanstack/react-query'
import { api } from '@/api/client'

export default function SellerDashboard() {

    const navigate = useNavigate()

    const { data: stats, isStatsLoading } = useQuery({
        queryKey: ['stats'],
        queryFn: () => api.get(`/carts/checkout/buy-now/product/${productId}`),
        staleTime: 1000 * 60 * 5
    })

    console.log(stats)

    return isStatsLoading ? <></> : (
        <>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3' onClick={() => navigate('./orders')}>
                <h2 className='text-5xl mb-1'>{stats?.confirmedOrderItems}</h2>
                <h3 className='font-medium mb-0.5 text-base'>Items yet to be shipped</h3>
            </div>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3' onClick={() => navigate('./orders')}>
                <h2 className='text-5xl mb-1'>{stats?.shippedOrderItems}</h2>
                <h3 className='font-medium mb-0.5 text-base'>Items yet to be delivered</h3>
            </div>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3'>
                <h2 className='text-5xl mb-1'>{stats?.productsOutOfStock}</h2>
                <h3 className='font-medium mb-0.5 text-base'>Products Out of Stock</h3>
            </div>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3'>
                <h2 className=''>{stats?.deliveredOrderItems}</h2>
                <h3 className=''>Items Delivered</h3>
            </div>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3'>
                <h2 className=''>{stats?.returnedOrderItems}</h2>
                <h3 className=''>Items Returned</h3>
            </div>
            <div className='flex flex-col border-2 bg-white p-2 shadow-2xl text-end basis-1/3'>
                <h2 className=''>₹{stats?.revenueToday}</h2>
                <h3 className=''>Total Revenue</h3>
            </div>
        </>
    )
}