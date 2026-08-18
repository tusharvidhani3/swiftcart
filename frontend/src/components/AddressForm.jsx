import { useEffect } from 'react'
import styles from '../styles/AddressForm.module.css'
import { useNavigate, useParams } from 'react-router'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api } from '@/api/client'
import z from 'zod'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from './ui/form'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Input } from './ui/input'
import { Button } from './ui/button'
import { RadioGroup, RadioGroupItem } from './ui/radio-group'
import { Label } from './ui/label'

export default function AddressForm() {

    const navigate = useNavigate()
    const { addressId } = useParams()

    const addressSchema = z.object({
        name: z.string().min(1, 'Name is required').min(2, 'Name must be at least 2 characters long').max(100, 'Name must not be longer than 100 characters').regex(/^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$/, "Name can only contain letters, spaces, hyphens (-), apostrophes (') and dots (.)"),
        mobileNumber: z.string().min(1, 'Mobile number is required').regex(/^[6-9]\d{9}$/, "Please enter a valid 10-digit mobile number"),
        pincode: z.string().min(1, "Pincode is required").regex(/^[1-9]\d{5}$/, 'Please enter a valid 6-digit pincode'),
        addressLine1: z.string().min(1, 'Please enter Building/Apartment/Company').min(3, "").max(100, "").regex(/^[A-Za-z0-9\s,./'-]+$/, "Only letters, numbers, spaces, and , . / ' - are allowed"),
        addressLine2: z.string().min(1, 'Please enter Area/Locality/Street').min(3, "").max(100, "").regex(/^[A-Za-z0-9\s,./'-]+$/, "Only letters, numbers, spaces, and , . / ' - are allowed"),
        city: z.string().min(1, 'City is required').regex(/^[A-Za-z\s-]{3,100}$/, "Please enter a valid city name"),
        state: z.string().min(1, 'State is required').regex(/^[A-Za-z\s-]{3,50}$/, "Please enter a valid state name")
    })

    const addressForm = useForm({
        resolver: zodResolver(addressSchema),
        defaultValues: { name: '', mobileNumber: '', pincode: '', addressLine1: '', addressLine2: '', city: '', state: '', addressType: 'HOME' }
    })

    const { data: address, isSuccess, isError, error } = useQuery({
        queryKey: ['addresses', 'list', addressId],
        queryFn: () => api.get(`/api/addresses/${addressId}`),
        enabled: !!addressId,
        staleTime: 1000 * 60 * 5
    })

    useEffect(() => {
        if (isSuccess && address)
            addressForm.reset(address)
    }, [isSuccess])

    const queryClient = useQueryClient()

    const { mutate: addAddress } = useMutation({
        mutationFn: (address) => api.post(`/api/addresses`, address),
        onSuccess: (addedAddress) => {
            queryClient.invalidateQueries({ queryKey: ['addresses', 'list'] })
            navigate('../')
        }
    })

    const { mutate: editAddress } = useMutation({
        mutationFn: (address) => api.put(`/api/addresses`, address),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['addresses', 'list'] })
            navigate('../')
        }
    })

    function onSubmit() {
        addressId ? editAddress(addressForm.getValues()) : addAddress(addressForm.getValues())
    }

    return (
        <Form {...addressForm}>
            <form id={styles.addressForm} control={addressForm.control} onSubmit={addressForm.handleSubmit(onSubmit)}>
                <FormField name='name' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>Full name</FormLabel>
                        <FormControl>
                            <Input type="text" autoComplete='name' {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />
                <FormField name='mobileNumber' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>Mobile number</FormLabel>
                        <FormControl>
                            <Input type="tel" minLength={10} maxLength={10} {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='pincode' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel htmlFor="pincode">Pincode</FormLabel>
                        <FormControl>
                            <Input type="text" inputMode="numeric" required={true} minLength={6} maxLength={6} {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='addressLine1' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>Flat, House no., Building, Company, Apartment</FormLabel>
                        <FormControl>
                            <Input type="text" {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='addressLine2' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>Area, Street, Sector, Village</FormLabel>
                        <FormControl>
                            <Input type="text" {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='city' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>City/District/Town</FormLabel>
                        <FormControl>
                            <Input type="text" {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='state' control={addressForm.control} render={({ field }) => (
                    <FormItem>
                        <FormLabel>State</FormLabel>
                        <FormControl>
                            <Input type="text" {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )} />

                <FormField name='addressType' control={addressForm.control} render={({ field }) => (
                    <FormItem className='flex flex-col gap-2'>
                        <div>Address Type</div>
                        <FormControl>
                            <RadioGroup value={field.value} onValueChange={field.onChange} className='flex h-10 w-full rounded-lg bg-[#E7E8E9] gap-0'>
                                <FormItem className={`flex justify-center ${field.value === 'HOME' && 'bg-[#1A237E] text-white'} transition-colors duration-500 ease-in-out rounded-lg basis-1/2 h-full`}>
                                    <RadioGroupItem className='sr-only hidden' id="home" value="HOME" />
                                    <Label htmlFor="home">Home</Label>
                                </FormItem>
                                <FormItem className={`flex justify-center ${field.value === 'WORK' && 'bg-[#1A237E] text-white'} transition-colors duration-500 ease-in-out rounded-lg basis-1/2 h-full`}>
                                    <RadioGroupItem className='sr-only hidden' id="work" value="WORK" />
                                    <Label htmlFor="work">Work</Label>
                                </FormItem>
                            </RadioGroup>
                        </FormControl>
                    </FormItem>
                )} />

                <Button className={styles.btnAddAddress}>{addressId ? 'Edit' : 'Add'} Address</Button>
            </form>
        </Form>
    )
}