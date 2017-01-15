#!/bin/bash

BACKUP_FILE="/home/carlos/compressed-backup/`date +%Y-%m-%d-%H-%M`-backup.tar.gz";

tar -czvf $BACKUP_FILE -C /home/carlos/backup/ . > /dev/null
