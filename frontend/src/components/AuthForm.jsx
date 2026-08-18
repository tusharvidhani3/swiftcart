import { Link, useNavigate, useSearchParams } from 'react-router'
import styles from '../styles/AuthForm.module.css'
import shoppingIcon from '../assets/icons/shopping-icon.svg'
import { useContext, useEffect, useMemo, useState } from 'react'
import UserContext from '../contexts/UserContext'
import { apiBaseUrl } from '../config'
import { Eye, EyeOff } from 'lucide-react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '@/api/client'
import z from 'zod'
import { zodResolver } from "@hookform/resolvers/zod"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from './ui/form'
import { Input } from './ui/input'
import { useForm } from 'react-hook-form'
import { Button } from './ui/button'
import axios from 'axios'
import GoogleLoginButton from './GoogleLoginButton'
import { handleWebGoogleLogin } from '@/config/firebase'

export default function AuthForm({ mode }) {

    const [showPassword, setShowPassword] = useState(false)
    const [searchParams] = useSearchParams()
    const redirectTo = searchParams.get('redirectTo')
    const isRegisterMode = useMemo(() => mode === "register", [mode])
    const navigate = useNavigate()
    const { user } = useContext(UserContext)
    const authSchema = z.object({
        email: z.email('Please enter a valid email').min(1, "Please enter email"),
        password: isRegisterMode ? z.string().min(1, 'Please enter password').min(8, 'Password must be at least 8 characters long').regex(/.*[a-z].*/, 'Password must contain a lowercase letter').regex(/.*[A-Z].*/, 'Password must contain an uppercase letter').regex(/.*\d.*/, 'Password must contain a digit').regex(/.*[^A-Za-z0-9].*/, 'Password must contain a special character') : z.string().min(1, 'Please enter password').regex(/^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/, 'Invalid username or password')
    })
    const authForm = useForm({
        resolver: zodResolver(authSchema),
        defaultValues: {
            email: "",
            password: ""
        }
    })

    function onSubmit() {
        if (isRegisterMode)
            register(authForm.getValues())
        else
            login(authForm.getValues())
    }

    const queryClient = useQueryClient()

    const { mutate: login } = useMutation({
        mutationFn: (authForm) => api.post('/auth/login', authForm),
        onSuccess: (user) => queryClient.setQueryData(['user'], user)
    })

    const { mutate: register } = useMutation({
        mutationFn: (authForm) => api.post('/auth/register', authForm),
        onSuccess: (user) => queryClient.setQueryData(['user'], user)
    })

    const { mutate: verifyToken } = useMutation({
        mutationFn: (idToken) => axios.post(`${apiBaseUrl}/api/auth/google`, { token: idToken }, { withCredentials: true }),
        onSuccess: (response) => {
            console.log(response)
            queryClient.setQueryData(['user'], response.data)
        }
    })

    const signInWithGoogle = async () => {
        const idToken = await handleWebGoogleLogin()
        if (idToken)
            verifyToken(idToken)
    }

    useEffect(() => {
        if (user)
            navigate(redirectTo || (user.role === 'ROLE_CUSTOMER' ? '/' : '/seller'))
    }, [user])

    return (
        <div className='flex items-center justify-center h-full pb-4 gap-0 w-full'>
            <div className={styles.banner}>
                <h1 className="font-bold text-3xl my-3">{isRegisterMode ? 'Create Your SwiftCart Account' : 'Login'}</h1>
                <p className={styles.bannerQuote}>{isRegisterMode ? 'Join SwiftCart - Where Smart Shopping Begins' : 'Get access to your Orders, Wishlist and Recommendations'}</p>
                <img className={styles.bannerImage} src={shoppingIcon} alt="shopping cart" />
            </div>
            <div className='flex flex-col items-center max-w-136 w-full bg-white p-6'>
                <Form {...authForm}>
                    <form id={styles.authForm} onSubmit={authForm.handleSubmit(onSubmit)}>
                        <h2 className={styles.formTitle}>{isRegisterMode ? "Register" : "Login"}</h2>
                        <GoogleLoginButton onClick={signInWithGoogle} />
                        <FormField control={authForm.control} name='email' render={({ field }) => (
                            <FormItem className="my-3 mt-8 w-9/10 gap-0">
                                <FormLabel className="authform-label">Email</FormLabel>
                                <FormControl>
                                    <Input type='email' autoComplete="username" className='authForm-input' placeholder="Enter name" {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )} />

                        <FormField control={authForm.control} name='password' render={({ field }) => (
                            <FormItem className="my-3 w-9/10 gap-0 relative">
                                <FormLabel className="authform-label">Password</FormLabel>
                                <FormControl>
                                    <Input type={showPassword ? 'text' : "password"} autoComplete="current-password" className='authForm-input' placeholder="Enter name" {...field} />
                                </FormControl>
                                {authForm.getValues("password") && <Button type='button' className="flex items-center justify-center bg-inherit absolute right-1 top-8 h-9" onMouseDown={e => e.preventDefault()} onClick={() => setShowPassword(showPassword => !showPassword)}>{showPassword ? <EyeOff /> : <Eye />}</Button>}
                                {(isRegisterMode && !field.value) ? <p className={styles.instruction}>Your password should be at least 8 characters long and include a mix of uppercase, lowercase, numbers, and special characters</p> : <FormMessage />}
                            </FormItem>
                        )} />
                        {isRegisterMode ? <Link to='/auth/login'>Existing User? Log in</Link> : <Link to='/auth/register'>New User? Create an account</Link>}
                        <Button className={styles.btnSubmit}>{isRegisterMode ? "Register" : "Login"}</Button>
                    </form>
                </Form>
            </div>
        </div>
    )
}