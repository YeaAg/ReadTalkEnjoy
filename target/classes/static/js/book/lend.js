// // 주문 버튼을 클릭했을 시
// orderButton.onclick = () => {
//     // request_order(2,1); // - DB 테스트용
//     request_payment(); // - 실제 결제 용
// };
//
// // 주문에 필요한 주문 객체 생성
// function create_order_obj(){
//     // 고유 주문 번호 생성
//     const uuid = crypto.randomUUID();
//     const orderId = uuid.split('-')[0] + (new Date().getSeconds()).toString().padStart(2, '0');
//     const products = document.getElementsByClassName('product');
//     // 주문명 제작
//     let orderName;
//     const productName = products[0].querySelector('.product-title-container > b').textContent.substring(0, 8);
//     if (products.length === 1) { // 주문 상품이 하나라면
//         orderName = `${productName}...`;
//     } else { // 주문 상품이 여러개라면
//         orderName = `${productName}... 외 ${products.length - 1}개`;
//     }
//
//     // 주문 상품 모으기
//     // const cartList = [...products].map(product => {
//     //         const [cartNo, cartAmount] = product.id.split('-');
//     //         const cartObject = {};
//     //         if(cartNo !== 'null'){
//     //             cartObject['no'] = +cartAmount;
//     //         }
//     //         cartObject['amount'] = +cartAmount;
//     //         return cartObject;
//     //     });
//
//     return {
//         id: orderId,
//         name: orderName,
//         shippingName: shippingName.value,
//         shippingTel: shippingPhone.value,
//         shippingAddr: shippingAddr.value,
//         shippingAddrDetail: shippingAddrDetail.value,
//         shippingPostcode: shippingPostCode.value,
//         ordererName: ordererName.value,
//         ordererTel: ordererPhone.value,
//         ordererEmail: ordererEmail.value
//     }
// }
//
// // 결제 요청(포트원)
// function request_payment() {
//     const orderObject = create_order_obj();
//     const totalPrice = +document.querySelector('.order-total-price-container').getAttribute('data');
//     console.log('주문내용:', orderObject);
//
//     IMP.request_pay(
//         {
//             channelKey: "channel-key-450b74cd-b45f-4c56-bca0-63c93e832757",
//             pg: "kakaopay",
//             merchant_uid: orderObject.id, // 주문 고유 번호
//             currency: "KRW",
//             name: orderObject.name,
//             amount: totalPrice,
//             buyer_email: orderObject.ordererEmail,
//             buyer_name: orderObject.ordererName,
//             buyer_tel: orderObject.ordererTel
//         },
//         function (response) {
//             // 결제 종료 시 호출되는 콜백 함수
//             // response.imp_uid 값으로 결제 단건조회 API를 호출하여 결제 결과를 확인하고,
//             // 결제 결과를 처리하는 로직을 작성합니다.
//             console.log('결제요청결과: ' + response);
//
//             if (response.error_code != null) {
//                 return alert(`결제에 실패하였습니다. 에러 내용: ${response.error_msg}`);
//             }
//             if (!response['success']) {
//                 alert('사용자가 결제를 취소했습니다');
//             }
//             // 결제 성공이라면 우리 어플로 주문 요청하기
//             orderObject['impUid'] = response['imp_uid'];
//             request_order(orderObject);
//         }
//     );
// }
//
// // 주문 요청(우리 어플리케이션)
// function request_order(requestBody) {
//     const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
//     // 우리 어플로 요청
//     fetch(`/order`, {
//         method: "POST",
//         headers: {
//             "Content-Type": "application/json",
//             "X-CSRF-TOKEN": csrfToken,
//         },
//         body: JSON.stringify(requestBody)
//     }).then(response => {
//         if (response.ok && response.status === 201) { // httpStatus.CREATED 했기 때문에
//             alert('주문이 완료되었습니다');
//             // 주문완료 페이지로 이동 or 주문내역 페이지로 이동
//             location.href = '/user/order/complete';
//         }
//     });
// }