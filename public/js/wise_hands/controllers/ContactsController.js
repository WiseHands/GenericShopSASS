angular.module('WiseHands')
    .controller('ContactsController', function ($scope, $route, $http, $location) {
        $scope.$route = $route;
        $scope.loading = true;

        $scope.activeShop = {
            domain: '',
            shopName: ''
        };
        //
        // $http({
        //     method: 'GET',
        //     url: '/delivery',
        //     headers: {
        //         'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
        //         'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
        //     }
        // })
        //     .then(function successCallback(response) {
        //         $scope.loading = false;
        //         $scope.delivery = response.data;
        //     }, function errorCallback(data) {
        //         $scope.loading = false;
        //         $scope.status = 'Щось пішло не так...';
        //     });
        // $scope.setNewPostOptions = function () {
        //     $scope.loading = true;
        //     $http({
        //         method: 'PUT',
        //         url: '/delivery',
        //         data: $scope.delivery,
        //         headers: {
        //             'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
        //             'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
        //         }
        //     })
        //         .then(function successCallback(response) {
        //             $scope.loading = false;
        //             $location.path('/delivery');
        //         }, function errorCallback(error) {
        //             $scope.loading = false;
        //             console.log(error);
        //         });
        //
        // };
        $http({
            method: 'GET',
            url: '/shop/details',
            headers: {
                'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
            }
        })
            .then(function successCallback(response) {
                $scope.activeShop = response.data;

            }, function errorCallback(data) {
                $scope.status = 'Щось пішло не так...';
            });

        $scope.getUrl = function (shop) {
            return  window.location.protocol + '//' + shop.domain + ':' + window.location.port;
        }
    });
