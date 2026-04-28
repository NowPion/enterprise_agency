<template>
	<view class="container">
		<!-- 头像 -->
		<view class="profile-item" @click="chooseAvatar">
			<text class="item-label">头像</text>
			<view class="item-right">
				<image class="avatar" :src="getFullImageUrl(userInfo.avatar)" mode="aspectFill"></image>
				<text class="arrow">›</text>
			</view>
		</view>
		
		<!-- 昵称 -->
		<view class="profile-item" @click="editNickname">
			<text class="item-label">昵称</text>
			<view class="item-right">
				<text class="item-value">{{ userInfo.nickname || '未设置' }}</text>
				<text class="arrow">›</text>
			</view>
		</view>
		
		<!-- 性别 -->
		<view class="profile-item" @click="showGenderPicker">
			<text class="item-label">性别</text>
			<view class="item-right">
				<text class="item-value">{{ genderText }}</text>
				<text class="arrow">›</text>
			</view>
		</view>
		
		<!-- 手机号 -->
		<view class="profile-item">
			<text class="item-label">手机号</text>
			<view class="item-right">
				<text class="item-value">{{ userInfo.phone || '未绑定' }}</text>
			</view>
		</view>
		
		<!-- 用户ID -->
		<view class="profile-item">
			<text class="item-label">用户ID</text>
			<view class="item-right">
				<text class="item-value">{{ userInfo.id || '-' }}</text>
			</view>
		</view>
		
		<!-- 注册时间 -->
		<view class="profile-item">
			<text class="item-label">注册时间</text>
			<view class="item-right">
				<text class="item-value">{{ userInfo.createTime || '-' }}</text>
			</view>
		</view>
		
		<!-- 昵称编辑弹窗 -->
		<view class="modal" v-if="showNicknameModal" @click="showNicknameModal = false">
			<view class="modal-content" @click.stop>
				<text class="modal-title">修改昵称</text>
				<input class="modal-input" v-model="tempNickname" placeholder="请输入昵称" maxlength="20" />
				<view class="modal-btns">
					<button class="modal-btn cancel" @click="showNicknameModal = false">取消</button>
					<button class="modal-btn confirm" @click="saveNickname">确定</button>
				</view>
			</view>
		</view>
		
		<!-- 性别选择弹窗 -->
		<view class="modal" v-if="showGenderModal" @click="showGenderModal = false">
			<view class="modal-content" @click.stop>
				<text class="modal-title">选择性别</text>
				<view class="gender-list">
					<view class="gender-item" :class="{ active: tempGender === 1 }" @click="tempGender = 1">
						<text>男</text>
						<text class="check" v-if="tempGender === 1">✓</text>
					</view>
					<view class="gender-item" :class="{ active: tempGender === 2 }" @click="tempGender = 2">
						<text>女</text>
						<text class="check" v-if="tempGender === 2">✓</text>
					</view>
					<view class="gender-item" :class="{ active: tempGender === 0 }" @click="tempGender = 0">
						<text>保密</text>
						<text class="check" v-if="tempGender === 0">✓</text>
					</view>
				</view>
				<view class="modal-btns">
					<button class="modal-btn cancel" @click="showGenderModal = false">取消</button>
					<button class="modal-btn confirm" @click="saveGender">确定</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { userApi } from '@/api/index.js'
import { uploadFile } from '@/utils/request.js'
import { getFullImageUrl } from '@/utils/image.js'

export default {
	data() {
		return {
			userInfo: {
				id: 0,
				nickname: '',
				avatar: '',
				gender: 0,
				phone: '',
				createTime: ''
			},
			showNicknameModal: false,
			showGenderModal: false,
			tempNickname: '',
			tempGender: 0
		}
	},
	computed: {
		genderText() {
			const map = { 0: '保密', 1: '男', 2: '女' }
			return map[this.userInfo.gender] || '保密'
		}
	},
	onLoad() {
		this.loadUserInfo()
	},
	methods: {
		// 加载用户信息
		async loadUserInfo() {
			// 先从本地获取
			const localUser = uni.getStorageSync('userInfo')
			if (localUser) {
				this.userInfo = { ...this.userInfo, ...localUser }
			}
			
			// 从服务器获取最新信息
			try {
				const res = await userApi.getUserInfo()
				if (res.code === 0) {
					this.userInfo = {
						id: res.data.id,
						nickname: res.data.nickname,
						avatar: res.data.avatar,
						gender: res.data.gender || 0,
						phone: res.data.phone,
						createTime: res.data.createTime
					}
					// 更新本地存储
					const stored = uni.getStorageSync('userInfo') || {}
					uni.setStorageSync('userInfo', { ...stored, ...this.userInfo })
				}
			} catch (e) {
				console.error('获取用户信息失败', e)
			}
		},
		
		// 选择头像
		chooseAvatar() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempPath = res.tempFilePaths[0]
					this.uploadAvatar(tempPath)
				}
			})
		},
		
		// 上传头像
		async uploadAvatar(filePath) {
			uni.showLoading({ title: '上传中...' })
			try {
				// 调用上传接口
				const uploadRes = await uploadFile({ 
					url: '/common/upload', 
					filePath,
					name: 'file'
				})
				
				if (uploadRes.code === 0 && uploadRes.data) {
					const avatarUrl = uploadRes.data.url || uploadRes.data
					
					// 更新用户信息
					await this.updateUserInfo({ avatarUrl })
					this.userInfo.avatar = avatarUrl
					
					uni.hideLoading()
					uni.showToast({ title: '头像已更新', icon: 'success' })
				} else {
					throw new Error(uploadRes.msg || '上传失败')
				}
			} catch (e) {
				uni.hideLoading()
				console.error('上传头像失败', e)
				uni.showToast({ title: '上传失败', icon: 'none' })
			}
		},
		
		// 编辑昵称
		editNickname() {
			this.tempNickname = this.userInfo.nickname
			this.showNicknameModal = true
		},
		
		// 保存昵称
		async saveNickname() {
			if (!this.tempNickname.trim()) {
				uni.showToast({ title: '请输入昵称', icon: 'none' })
				return
			}
			
			try {
				await this.updateUserInfo({ nickName: this.tempNickname })
				this.userInfo.nickname = this.tempNickname
				this.showNicknameModal = false
				uni.showToast({ title: '昵称已更新', icon: 'success' })
			} catch (e) {
				uni.showToast({ title: '更新失败', icon: 'none' })
			}
		},
		
		// 显示性别选择
		showGenderPicker() {
			this.tempGender = this.userInfo.gender
			this.showGenderModal = true
		},
		
		// 保存性别
		async saveGender() {
			try {
				await this.updateUserInfo({ gender: this.tempGender })
				this.userInfo.gender = this.tempGender
				this.showGenderModal = false
				uni.showToast({ title: '性别已更新', icon: 'success' })
			} catch (e) {
				uni.showToast({ title: '更新失败', icon: 'none' })
			}
		},
		
		// 更新用户信息
		async updateUserInfo(data) {
			const res = await userApi.updateUserInfo(data)
			if (res.code === 0) {
				// 更新本地存储
				const stored = uni.getStorageSync('userInfo') || {}
				if (data.nickName) stored.nickname = data.nickName
				if (data.avatarUrl) stored.avatar = data.avatarUrl
				if (data.gender !== undefined) stored.gender = data.gender
				uni.setStorageSync('userInfo', stored)
				return true
			}
			throw new Error(res.msg || '更新失败')
		},
		getFullImageUrl
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

.profile-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	background: #fff;
	border-bottom: 1rpx solid #f0f0f0;
}

.item-label {
	font-size: 30rpx;
	color: #333;
}

.item-right {
	display: flex;
	align-items: center;
}

.item-value {
	font-size: 28rpx;
	color: #999;
	margin-right: 10rpx;
}

.avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 10rpx;
}

.arrow {
	font-size: 32rpx;
	color: #ccc;
}

/* 弹窗样式 */
.modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0,0,0,0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 999;
}

.modal-content {
	width: 80%;
	background: #fff;
	border-radius: 20rpx;
	padding: 40rpx;
}

.modal-title {
	font-size: 34rpx;
	font-weight: bold;
	color: #333;
	text-align: center;
	display: block;
	margin-bottom: 30rpx;
}

.modal-input {
	width: 100%;
	height: 80rpx;
	border: 1rpx solid #e0e0e0;
	border-radius: 10rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}

.modal-btns {
	display: flex;
	gap: 20rpx;
	margin-top: 40rpx;
}

.modal-btn {
	flex: 1;
	height: 80rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-btn.cancel {
	background: #f5f5f5;
	color: #666;
}

.modal-btn.confirm {
	background: #2196F3;
	color: #fff;
}

.gender-list {
	margin-bottom: 20rpx;
}

.gender-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
	font-size: 30rpx;
	color: #333;
}

.gender-item.active {
	color: #2196F3;
}

.gender-item .check {
	color: #2196F3;
	font-weight: bold;
}
</style>
""