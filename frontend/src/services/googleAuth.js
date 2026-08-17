import { auth } from "@/config/firebase";
import { GoogleAuthProvider, signInWithCredential } from "firebase/auth";

export async function handleWebGoogleLogin(credentialResponse) {
    try {
        const googleJwt = credentialResponse?.credential
        if (!googleJwt)
            throw new Error('No credential returned from Google Web Sign-In');
        const credential = GoogleAuthProvider.credential(googleJwt)
        const userCredential = await signInWithCredential(auth, credential)
        const firebaseIdToken = await userCredential.user.getIdToken()
        return firebaseIdToken
    }
    catch (error) {
        console.error('Web Google Sign-In Error:', error);
        throw error;
    }
}