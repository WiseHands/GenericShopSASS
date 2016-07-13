    angular.module('WiseHands')
        .controller('SingleOrderController', ['$http', '$scope', '$routeParams',
            function($http, $scope, $routeParams, $route) {
                $scope.$route = $route;
                $scope.uuid = $routeParams.uuid;
                $scope.loading = true;
                $http({
                    method: 'GET',
                    url: '/order/' + $routeParams.uuid,
                    headers: {
                        'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                        'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                    }
                })
                    .then(function successCallback(response) {
                        $scope.loading = false;
                        var data = response.data;
                        if(data.length === 0) {
                            $scope.status = 'Замовлення відсутні';
                        } else {
                            $scope.order = response.data;
                        }
                    }, function errorCallback(data) {
                        $scope.loading = false;
                        $scope.status = 'Щось пішло не так...';
                    });
                $scope.deleteMessage = 'Ви дійсно хочете видалити дане замовлення?';
                $scope.hideModal = function () {
                    $('#deleteProduct').modal('hide');
                    $('body').removeClass('modal-open');
                    $('.modal-backdrop').remove();
                };
                $scope.deleteOrder = function () {
                    $http({
                        method: 'DELETE',
                        url: '/order/' + $routeParams.uuid,
                        headers: {
                            'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                            'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                        }
                    })
                        .then(function successCallback(response) {
                            $scope.deleteMessage = 'Замовлення видалене.';
                            $scope.deleteButton = true;
                        }, function errorCallback(error) {
                            console.log(error);
                        });
                };
                $scope.orderState = function(order){
                    if (!order) return;
                    if (order.state === "NEW"){
                        return 'Нове';
                    } else if (order.state === "PAYED") {
                        return 'Оплачено';
                    } else if (order.state === "CANCELLED") {
                        return 'Скасовано';
                    } else if (order.state === "SHIPPED") {
                        return 'Надіслано';
                    } else if (order.state === "RETURNED") {
                        return 'Повернено';
                    }
                };
                $scope.payedOrder = function () {
                    $scope.loading = true;
                    $http({
                        method: 'PUT',
                        url: '/order/' + $routeParams.uuid + '/payed',
                        headers: {
                            'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                            'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                        }
                    })
                        .then(function successCallback(response){
                            $scope.loading = false;
                            $scope.order = response.data;
                        }, function errorCallback(error){
                            $scope.loading = false;
                            console.log(error);
                        });
                };
                $scope.cancelledOrder = function () {
                    $scope.loading = true;
                    $http({
                        method: 'PUT',
                        url: '/order/' + $routeParams.uuid + '/cancelled',
                        headers: {
                            'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                            'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                        }
                    })
                        .then(function successCallback(response){
                            $scope.loading = false;
                            $scope.order = response.data;
                        }, function errorCallback(error){
                            $scope.loading = false;
                            console.log(error);
                        });
                };
                $scope.shippedOrder = function () {
                    $scope.loading = true;
                    $http({
                        method: 'PUT',
                        url: '/order/' + $routeParams.uuid + '/shipped',
                        headers: {
                            'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                            'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                        }
                    })
                        .then(function successCallback(response){
                            $scope.loading = false;
                            $scope.order = response.data;
                        }, function errorCallback(error){
                            $scope.loading = false;
                            console.log(error);
                        });
                };
                $scope.returnedOrder = function () {
                    $scope.loading = true;
                    $http({
                        method: 'PUT',
                        url: '/order/' + $routeParams.uuid + '/returned',
                        headers: {
                            'X-AUTH-TOKEN': localStorage.getItem('X-AUTH-TOKEN'),
                            'X-AUTH-USER-ID': localStorage.getItem('X-AUTH-USER-ID')
                        }
                    })
                        .then(function successCallback(response){
                            $scope.loading = false;
                            $scope.order = response.data;
                        }, function errorCallback(error){
                            $scope.loading = false;
                            console.log(error);
                        });
                };
            }]);
