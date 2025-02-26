package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/nacos-group/nacos-sdk-go/clients"
	"github.com/nacos-group/nacos-sdk-go/clients/naming_client"
	"github.com/nacos-group/nacos-sdk-go/common/constant"
	"github.com/nacos-group/nacos-sdk-go/vo"
)

const (
	nacosHost   = "127.0.0.1"
	nacosPort   = 8848
	serviceIP   = "127.0.0.1" // 当前服务IP
	servicePort = 8090        // 当前服务端口
	serviceName = "go-demo-service"
)

var namingClient naming_client.INamingClient

func main() {
	// 初始化Nacos客户端
	initNacosClient()

	// 创建Gin引擎
	r := gin.Default()

	// 注册路由
	r.GET("/demo/hello", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"message": "Hello World!",
		})
	})

	// 健康检查接口
	r.GET("/demo/health", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status": "UP",
		})
	})

	// 注册服务到Nacos
	registerService()

	// 设置优雅关闭
	server := &http.Server{
		Addr:    fmt.Sprintf(":%d", servicePort),
		Handler: r,
	}

	go func() {
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			panic(err)
		}
	}()

	// 等待中断信号
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	// 反注册服务
	deregisterService()

	// 关闭服务器
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	server.Shutdown(ctx)
}

func initNacosClient() {
	// 创建Nacos客户端配置
	sc := []constant.ServerConfig{
		{
			IpAddr: nacosHost,
			Port:   nacosPort,
		},
	}

	cc := constant.ClientConfig{
		NamespaceId:         "public",
		TimeoutMs:           5000,
		NotLoadCacheAtStart: true,
		LogDir:              "/tmp/nacos/log",
		CacheDir:            "/tmp/nacos/cache",
		LogLevel:            "info",
	}

	// 创建服务发现客户端
	var err error
	namingClient, err = clients.NewNamingClient(
		vo.NacosClientParam{
			ClientConfig:  &cc,
			ServerConfigs: sc,
		},
	)

	if err != nil {
		panic(err)
	}
}

func registerService() {
	success, err := namingClient.RegisterInstance(vo.RegisterInstanceParam{
		Ip:          serviceIP,
		Port:        uint64(servicePort),
		ServiceName: serviceName,
		Weight:      10,
		Enable:      true,
		Healthy:     true,
		Ephemeral:   true,
		Metadata:    map[string]string{"version": "1.0"},
		ClusterName: "DEFAULT",
	})

	if !success || err != nil {
		panic("Register service failed: " + err.Error())
	}
	fmt.Println("Service registered successfully")
}

func deregisterService() {
	success, err := namingClient.DeregisterInstance(vo.DeregisterInstanceParam{
		Ip:          serviceIP,
		Port:        uint64(servicePort),
		ServiceName: serviceName,
		Cluster:     "DEFAULT",
		Ephemeral:   true,
	})

	if !success || err != nil {
		fmt.Println("Deregister service failed:", err)
		return
	}
	fmt.Println("Service deregistered successfully")
}
