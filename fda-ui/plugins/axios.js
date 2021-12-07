export default function ({ $axios, redirect }) {
  console.log('axios intercepter args', arguments)
}

// export default function (item) {
//   $axios.onError(error => {
//     if(error.response.status === 500) {
//       redirect('/sorry')
//     }
//   })
// }
