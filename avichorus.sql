USE [avichorus]
GO
/****** Object:  Table [dbo].[projects]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[projects](
	[nProjectID] [int] IDENTITY(1,1) NOT NULL,
	[chName] [varchar](120) NOT NULL,
	[chAbbrev] [varchar](12) NOT NULL,
	[chDescription] [text] NOT NULL,
	[tsCreated] [bigint] NOT NULL,
	[fkUserID] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[nProjectID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[projects] ON
INSERT [dbo].[projects] ([nProjectID], [chName], [chAbbrev], [chDescription], [tsCreated], [fkUserID]) VALUES (1, N'ABMI Test Project', N'ABMI-TEST', N'A Sample project', 1465229570, 1)
SET IDENTITY_INSERT [dbo].[projects] OFF
/****** Object:  Table [dbo].[files]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[files](
	[nFileID] [int] IDENTITY(1,1) NOT NULL,
	[chName] [varchar](255) NULL,
	[chPath] [varchar](255) NULL,
	[chOriginalFileName] [varchar](255) NULL,
	[chMimeType] [varchar](25) NULL,
PRIMARY KEY CLUSTERED 
(
	[nFileID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[files] ON
INSERT [dbo].[files] ([nFileID], [chName], [chPath], [chOriginalFileName], [chMimeType]) VALUES (1, N'Canada-AB-BCR11-2012-Jun-13-pc1530_[-111.885,51.5229].mp3', N'', N'Canada-AB-BCR11-2012-Jun-13-pc1530_[-111.885,51.5229].mp3', N'audio/mpeg')
INSERT [dbo].[files] ([nFileID], [chName], [chPath], [chOriginalFileName], [chMimeType]) VALUES (2, N'Canada-ON-2011-Jun-15-pc1578_[-92.8851,50.272].wav', N'', N'Canada-ON-2011-Jun-15-pc1578_[-92.8851,50.272].wav', N'audio/x-wav')
SET IDENTITY_INSERT [dbo].[files] OFF
/****** Object:  Table [dbo].[users]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[users](
	[nUserID] [int] IDENTITY(1,1) NOT NULL,
	[chFirstname] [varchar](50) NULL,
	[chSurname] [varchar](50) NULL,
	[chEmail] [varchar](70) NULL,
	[chUsername] [varchar](100) NULL,
	[chPassword] [varchar](35) NULL,
PRIMARY KEY CLUSTERED 
(
	[nUserID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[users] ON
INSERT [dbo].[users] ([nUserID], [chFirstname], [chSurname], [chEmail], [chUsername], [chPassword]) VALUES (1, N'Paul', N'Morrill', N'paul@ravenforge.ca', N'pmorrill', N'')
SET IDENTITY_INSERT [dbo].[users] OFF
/****** Object:  Table [dbo].[specs]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[specs](
	[nSpecID] [int] IDENTITY(1,1) NOT NULL,
	[chCName] [varchar](50) NULL,
	[chAbbrev] [varchar](10) NULL,
	[chScientific] [varchar](120) NULL,
PRIMARY KEY CLUSTERED 
(
	[nSpecID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[specs] ON
INSERT [dbo].[specs] ([nSpecID], [chCName], [chAbbrev], [chScientific]) VALUES (1, N'American Crow', N'AMCR', N'Corvus brachyrhynchos')
INSERT [dbo].[specs] ([nSpecID], [chCName], [chAbbrev], [chScientific]) VALUES (2, N'American Robin', N'AMRO', N'Turdus migratorius')
INSERT [dbo].[specs] ([nSpecID], [chCName], [chAbbrev], [chScientific]) VALUES (3, N'Western Meadowlark', N'WEME', N'Sturnella neglecta')
SET IDENTITY_INSERT [dbo].[specs] OFF
/****** Object:  Table [dbo].[recordings]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[recordings](
	[nRecordingID] [int] IDENTITY(1,1) NOT NULL,
	[chName] [varchar](120) NOT NULL,
	[chSiteName] [varchar](120) NOT NULL,
	[chHabitat] [varchar](120) NOT NULL,
	[chTimeClass] [varchar](15) NOT NULL,
	[fkFileID] [int] NOT NULL,
	[chSongMeterID] [varchar](25) NOT NULL,
	[fltLongitude] [float] NOT NULL,
	[fltLatitude] [float] NOT NULL,
	[nBCR] [int] NOT NULL,
	[chCountry] [varchar](50) NOT NULL,
	[chProvince] [varchar](50) NOT NULL,
	[dtDate] [datetime] NULL,
	[fkProjectID] [int] NOT NULL,
	[fkUserID] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[nRecordingID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[recordings] ON
INSERT [dbo].[recordings] ([nRecordingID], [chName], [chSiteName], [chHabitat], [chTimeClass], [fkFileID], [chSongMeterID], [fltLongitude], [fltLatitude], [nBCR], [chCountry], [chProvince], [dtDate], [fkProjectID], [fkUserID]) VALUES (1, N'Canada-AB-BCR11-2012-Jun-13-pc1530_[-111.885,51.5229].mp3', N'TestSiteOne', N'Grassland Shrubland and Agriculture', N'Dawn', 1, N'', -111.885, 51.5229, 10, N'Canada', N'AB', CAST(0x0000A06F00575760 AS DateTime), 1, 1)
INSERT [dbo].[recordings] ([nRecordingID], [chName], [chSiteName], [chHabitat], [chTimeClass], [fkFileID], [chSongMeterID], [fltLongitude], [fltLatitude], [nBCR], [chCountry], [chProvince], [dtDate], [fkProjectID], [fkUserID]) VALUES (2, N'Canada-ON-2011-Jun-15-pc1578_[-92.8851,50.272].wav', N'TestSiteTwo', N'', N'Morning', 2, N'', -92.8851, 50.272, 8, N'Canada', N'ON', CAST(0x00009F0300A8A3E0 AS DateTime), 1, 1)
SET IDENTITY_INSERT [dbo].[recordings] OFF
/****** Object:  Table [dbo].[tags]    Script Date: 06/09/2016 10:11:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tags](
	[nTagID] [int] IDENTITY(1,1) NOT NULL,
	[fkRecordingID] [int] NOT NULL,
	[fkSpecID] [int] NOT NULL,
	[nCount] [int] NOT NULL,
	[nBird] [int] NOT NULL,
	[chAltTaxa] [varchar](60) NOT NULL,
	[chConfidence] [varchar](30) NOT NULL,
	[chComment] [varchar](255) NOT NULL,
	[nStatus] [int] NOT NULL,
	[fltStart] [float] NOT NULL,
	[fltDuration] [float] NOT NULL,
	[fltBoxY] [float] NOT NULL,
	[fltHeight] [float] NOT NULL,
	[nChannel] [int] NOT NULL,
	[bStereoTag] [smallint] NOT NULL,
	[bTemporary] [smallint] NOT NULL,
	[bReference] [smallint] NOT NULL,
	[tsPublished] [bigint] NOT NULL,
	[tsCreated] [bigint] NOT NULL,
	[tsModified] [bigint] NOT NULL,
	[fkUserID] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[nTagID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Default [DF__files__chName__07C1F487]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[files] ADD  DEFAULT ('') FOR [chName]
GO
/****** Object:  Default [DF__files__chPath__08B618C0]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[files] ADD  DEFAULT ('') FOR [chPath]
GO
/****** Object:  Default [DF__files__chOrigina__09AA3CF9]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[files] ADD  DEFAULT ('') FOR [chOriginalFileName]
GO
/****** Object:  Default [DF__files__chMimeTyp__0A9E6132]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[files] ADD  DEFAULT ('text') FOR [chMimeType]
GO
/****** Object:  Default [DF__projects__chName__161013DE]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[projects] ADD  DEFAULT ('') FOR [chName]
GO
/****** Object:  Default [DF__projects__chAbbr__17043817]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[projects] ADD  DEFAULT ('') FOR [chAbbrev]
GO
/****** Object:  Default [DF__projects__chDesc__17F85C50]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[projects] ADD  DEFAULT ('') FOR [chDescription]
GO
/****** Object:  Default [DF__projects__tsCrea__18EC8089]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[projects] ADD  DEFAULT ((0)) FOR [tsCreated]
GO
/****** Object:  Default [DF__projects__fkUser__19E0A4C2]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[projects] ADD  DEFAULT ((0)) FOR [fkUserID]
GO
/****** Object:  Default [DF__recording__chNam__1EA559DF]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('') FOR [chName]
GO
/****** Object:  Default [DF__recording__chSit__1F997E18]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('') FOR [chSiteName]
GO
/****** Object:  Default [DF__recording__chHab__208DA251]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('') FOR [chHabitat]
GO
/****** Object:  Default [DF__recording__chTim__2181C68A]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('Unknown') FOR [chTimeClass]
GO
/****** Object:  Default [DF__recording__fkFil__236A0EFC]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [fkFileID]
GO
/****** Object:  Default [DF__recording__chSon__245E3335]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('') FOR [chSongMeterID]
GO
/****** Object:  Default [DF__recording__fltLo__2552576E]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [fltLongitude]
GO
/****** Object:  Default [DF__recording__fltLa__26467BA7]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [fltLatitude]
GO
/****** Object:  Default [DF__recordings__nBCR__273A9FE0]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [nBCR]
GO
/****** Object:  Default [DF__recording__chCou__282EC419]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('Canada') FOR [chCountry]
GO
/****** Object:  Default [DF__recording__chPro__2922E852]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ('') FOR [chProvince]
GO
/****** Object:  Default [DF__recording__fkPro__2A170C8B]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [fkProjectID]
GO
/****** Object:  Default [DF__recording__fkUse__2B0B30C4]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings] ADD  DEFAULT ((0)) FOR [fkUserID]
GO
/****** Object:  Default [DF__specs__chCName__0F63164F]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[specs] ADD  DEFAULT ('') FOR [chCName]
GO
/****** Object:  Default [DF__specs__chAbbrev__10573A88]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[specs] ADD  DEFAULT ('') FOR [chAbbrev]
GO
/****** Object:  Default [DF__specs__chScienti__114B5EC1]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[specs] ADD  DEFAULT ('') FOR [chScientific]
GO
/****** Object:  Default [DF__tags__fkRecordin__30C40A1A]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fkRecordingID]
GO
/****** Object:  Default [DF__tags__fkSpecID__32AC528C]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fkSpecID]
GO
/****** Object:  Default [DF__tags__nCount__33A076C5]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((1)) FOR [nCount]
GO
/****** Object:  Default [DF__tags__nBird__34949AFE]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((1)) FOR [nBird]
GO
/****** Object:  Default [DF__tags__chAltTaxa__3588BF37]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ('') FOR [chAltTaxa]
GO
/****** Object:  Default [DF__tags__chConfiden__367CE370]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ('High') FOR [chConfidence]
GO
/****** Object:  Default [DF__tags__chComment__377107A9]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ('') FOR [chComment]
GO
/****** Object:  Default [DF__tags__nStatus__38652BE2]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [nStatus]
GO
/****** Object:  Default [DF__tags__fltStart__3959501B]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fltStart]
GO
/****** Object:  Default [DF__tags__fltDuratio__3A4D7454]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fltDuration]
GO
/****** Object:  Default [DF__tags__fltBoxY__3B41988D]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fltBoxY]
GO
/****** Object:  Default [DF__tags__fltHeight__3C35BCC6]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fltHeight]
GO
/****** Object:  Default [DF__tags__nChannel__3D29E0FF]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [nChannel]
GO
/****** Object:  Default [DF__tags__bStereoTag__3E1E0538]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [bStereoTag]
GO
/****** Object:  Default [DF__tags__bTemporary__3F122971]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [bTemporary]
GO
/****** Object:  Default [DF__tags__bReference__40064DAA]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [bReference]
GO
/****** Object:  Default [DF__tags__tsPublishe__40FA71E3]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [tsPublished]
GO
/****** Object:  Default [DF__tags__tsCreated__41EE961C]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [tsCreated]
GO
/****** Object:  Default [DF__tags__tsModified__42E2BA55]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [tsModified]
GO
/****** Object:  Default [DF__tags__fkUserID__44CB02C7]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags] ADD  DEFAULT ((0)) FOR [fkUserID]
GO
/****** Object:  Default [DF__users__chFirstna__7F2CAE86]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[users] ADD  DEFAULT ('') FOR [chFirstname]
GO
/****** Object:  Default [DF__users__chSurname__0020D2BF]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[users] ADD  DEFAULT ('') FOR [chSurname]
GO
/****** Object:  Default [DF__users__chEmail__0114F6F8]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[users] ADD  DEFAULT ('') FOR [chEmail]
GO
/****** Object:  Default [DF__users__chUsernam__02091B31]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[users] ADD  DEFAULT ('') FOR [chUsername]
GO
/****** Object:  Default [DF__users__chPasswor__02FD3F6A]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[users] ADD  DEFAULT ('') FOR [chPassword]
GO
/****** Object:  ForeignKey [FK__recording__fkFil__2275EAC3]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[recordings]  WITH CHECK ADD FOREIGN KEY([fkFileID])
REFERENCES [dbo].[files] ([nFileID])
GO
/****** Object:  ForeignKey [FK__tags__fkRecordin__2FCFE5E1]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags]  WITH CHECK ADD FOREIGN KEY([fkRecordingID])
REFERENCES [dbo].[recordings] ([nRecordingID])
GO
/****** Object:  ForeignKey [FK__tags__fkSpecID__31B82E53]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags]  WITH CHECK ADD FOREIGN KEY([fkSpecID])
REFERENCES [dbo].[specs] ([nSpecID])
GO
/****** Object:  ForeignKey [FK__tags__fkUserID__43D6DE8E]    Script Date: 06/09/2016 10:11:31 ******/
ALTER TABLE [dbo].[tags]  WITH CHECK ADD FOREIGN KEY([fkUserID])
REFERENCES [dbo].[users] ([nUserID])
GO
