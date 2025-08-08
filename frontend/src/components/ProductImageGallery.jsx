import { Swiper, SwiperSlide } from 'swiper/react';
import { Pagination } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/pagination';
import useMediaQuery from '../hooks/useMediaQuery'
import { useState } from 'react';
import styles from '../styles/ProductDetails.module.css'

export default function ProductImageGallery({ imageUrls }) {

    const isMobile = useMediaQuery('(max-width: 767px)')
    const [selectedIndex, setSelectedIndex] = useState(0)

    return isMobile ? (
        <Swiper slidesPerView={1} spaceBetween={10} modules={[Pagination]} pagination={{clickable: true}} className={styles.productImageSwiper}>
            {imageUrls.map((imageUrl, i) => <SwiperSlide key={i}><img className={styles.productImageSlide} src={imageUrl} style={{ width: '100%', height: 'auto', objectFit: 'contain' }} /></SwiperSlide>)}
        </Swiper>
    )
    :
    (
        <div className={styles.productImageGallery}>
            <div className={styles.productThumbnailsContainer}>
                {imageUrls.map((imageUrl, i) => <img className={`${styles.productThumbnail} ${selectedIndex === i ? styles.selected : ''}`} src={imageUrl} alt="" key={i} onMouseEnter={() => setSelectedIndex(i)} />)}
                <button><img src=".." alt="" /></button>
            </div>
            <div className={styles.mainImageContainer}>
                <img src={imageUrls[selectedIndex]} alt="" className={styles.mainImage} />
            </div>
        </div>
    )
}