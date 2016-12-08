angular.module('WiseHands')
    .controller('PaymentController', ['$scope', '$http', 'signout', 'sideNavInit', 'shared', function ($scope, $http, signout, sideNavInit, shared) {
        $scope.loading = true;

        $http({
            method: 'GET',
            url: '/payment/detail',
            headers: {
                'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
            }
        })
            .then(function successCallback(response) {
                $scope.payment = response.data;
                $scope.loading = false;
            }, function errorCallback(data) {
                console.log(data);
                $scope.loading = false;
                signout.signOut();
            });

            $http({
                method: 'GET',
                url: '/coupons',
                headers: {
                    'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                    'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                }
            })
                .then(function successCallback(response) {
                    $scope.coupons = response.data;
                    $scope.result = {};
                    for(var i=0; i<$scope.coupons.length; i++){
                        var coupon = $scope.coupons[i];
                        if(!$scope.result[coupon.percentDiscount]){
                            $scope.result[coupon.percentDiscount] = [coupon];
                        } else {
                            $scope.result[coupon.percentDiscount].push(coupon);
                        }
                    }
                    $scope.loading = false;
                }, function errorCallback(response) {
                    if (response.data === 'Invalid X-AUTH-TOKEN') {
                        signout.signOut();
                    }
                    $scope.loading = false;
                    console.log(response);
                });


        $scope.setPaymentOptions = function () {
            $scope.loading = true;
            $http({
                method: 'PUT',
                url: '/payment/update',
                data: $scope.payment,
                headers: {
                    'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                    'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                }
            })
                .then(function successCallback(response) {
                    $scope.payment = response.data;
                    $scope.loading = false;
                }, function errorCallback(response) {
                    if (response.data === 'Invalid X-AUTH-TOKEN') {
                        signout.signOut();
                    }
                    $scope.loading = false;
                    console.log(response);
                });

        };
        $scope.createCoupons = function () {
            $scope.loading = true;
            $http({
                method: 'POST',
                url: '/coupons',
                data: $scope.discount,
                headers: {
                    'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                    'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                }
            })
                .then(function successCallback(response) {
                    $scope.coupons = response.data;
                    $scope.result = {};
                    for(var i=0; i<$scope.coupons.length; i++){
                        var coupon = $scope.coupons[i];
                        if(!$scope.result[coupon.percentDiscount]){
                            $scope.result[coupon.percentDiscount] = [coupon];
                        } else {
                            $scope.result[coupon.percentDiscount].push(coupon);
                        }
                    }
                    $scope.discount = {};
                    $scope.loading = false;
                }, function errorCallback(response) {
                    if (response.data === 'Invalid X-AUTH-TOKEN') {
                        signout.signOut();
                    }
                    $scope.loading = false;
                    console.log(response);
                });

        };
        $scope.setDiscountCard = function (coupon) {
            shared.setDiscountCards(coupon);
        };

        $scope.deleteMessage = 'Ви дійсно хочете видалити всі купони?';
        $scope.deleteButton = true;
        $scope.hideModal3 = function () {
            $('#deleteAllCoupons').modal('hide');
            $('body').removeClass('modal-open');
            $('.modal-backdrop').remove();
        };
        $scope.deleteAllCoupons = function () {
            $scope.deleteButton = false;
            $scope.loading = true;
            $http({
                method: 'DELETE',
                url: '/coupons',
                headers: {
                    'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                    'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                }
            })
                .then(function successCallback(response) {
                    $scope.coupons = [];
                    $scope.result = {};
                    $scope.loading = false;
                    $scope.succesfullDelete = true;
                    $scope.deleteMessage = 'Купони видалені.';

                }, function errorCallback(response) {
                    if (response.data === 'Invalid X-AUTH-TOKEN') {
                        signout.signOut();
                    }
                    $scope.modalSpinner = false;
                    console.log(response);
                });

        };
        sideNavInit.sideNav();
    }]);


