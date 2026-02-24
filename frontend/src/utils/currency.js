export const formatPaiseToRupees = (paise) => {
  if (!paise && paise !== 0) return '';
  
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
  }).format(paise / 100);
};

export const handlePriceInput = (decimalValue) => {
  const floatValue = parseFloat(decimalValue);
  const priceInPaise = Math.round(floatValue * 100);
  return priceInPaise;
};